#include <stdio.h>
#include <stdlib.h> 
#include <string.h>
#include <unistd.h>
#include <time.h>
#include "disk_emu.h"
#include "sfs_api.h"

//in-memory data structures
DescriptorTable descriptor_table;
INodeTable *inode_table_cache;
Directory *root_directory_cache;

char FreeBitmap[FILE_SYSTEM_SIZE];
int getnextfilename_index = 0;

void mksfs(int fresh){
    if(fresh == 1){
        //creates a fresh disk
        init_fresh_disk(FILENAME, BLOCK_SIZE, FILE_SYSTEM_SIZE);

        //creates the superblock
        SuperBlock *superblock_buffer = malloc(BLOCK_SIZE);
        (*superblock_buffer).magic = 0xACBD0005;
        (*superblock_buffer).block_size = BLOCK_SIZE;
        (*superblock_buffer).file_system_size = FILE_SYSTEM_SIZE;
        (*superblock_buffer).inode_table_entries = FILE_SYSTEM_MAX_INODE_ENTRIES;
        (*superblock_buffer).root_directory_inode = 0;

        //writes the superblock
        write_blocks(0, 1, superblock_buffer);
        free(superblock_buffer);

        //creates the root directory
        Directory *root_directory_buffer = malloc(ROOT_DIRECTORY_SIZE*BLOCK_SIZE);
        (*root_directory_buffer).num_entries = 0;

        //initializes the root directory
        for(int i = 0; i < FILE_SYSTEM_MAX_DIRECTORY_ENTRIES; i++){
            DirectoryEntry empty_entry;

            empty_entry.is_used = 0;
            empty_entry.inode = -1;
            empty_entry.filename = "";

            (*root_directory_buffer).directory_entries[i] = empty_entry;
        }

        int default_root_directory_block = INODE_TABLE_SIZE+1;

        //writes the root directory
        write_blocks(default_root_directory_block, ROOT_DIRECTORY_SIZE, root_directory_buffer);
        free(root_directory_buffer);

        //creates the root directory inode
        INode root_directory_inode;

        //file mode reference: https://www.gnu.org/software/libc/manual/html_node/Permission-Bits.html
        root_directory_inode.is_used = 1;
        root_directory_inode.mode = S_IFDIR | S_IRWXU | S_IRWXG | S_IRWXO;
        root_directory_inode.link_count = 1;
        root_directory_inode.size = sizeof(Directory);
        root_directory_inode.ind_pointer = -1;

        //initializes the root directory inode block pointers
        for(int i = 0; i < ROOT_DIRECTORY_SIZE; i++){
            root_directory_inode.pointers[i] = default_root_directory_block+i;
        }

        //creates the inode table
        INodeTable *inode_table_buffer = malloc(INODE_TABLE_SIZE*BLOCK_SIZE);

        //initializes the inode table
        (*inode_table_buffer).num_entries = 0;

        for(int i = 0; i < FILE_SYSTEM_MAX_INODE_ENTRIES; i++){
            INode empty_inode;

            //file mode reference: https://www.gnu.org/software/libc/manual/html_node/Permission-Bits.html
            empty_inode.is_used = 0;
            empty_inode.mode = S_IFDIR | S_IRWXU | S_IRWXG | S_IRWXO;
            empty_inode.link_count = 0;
            empty_inode.size = 0;
            empty_inode.ind_pointer = -1;
            
            for(int j = 0; j < INODE_SIZE; j++){
                empty_inode.pointers[j] = -1;
            }

            (*inode_table_buffer).inodes[i] = empty_inode;
        }

        (*inode_table_buffer).inodes[0] = root_directory_inode;
        (*inode_table_buffer).num_entries = 1;

        //writes the inode table
        write_blocks(1, INODE_TABLE_SIZE, inode_table_buffer);
        free(inode_table_buffer);

        //initializes the free bitmap
        for(int i = 0; i < FILE_SYSTEM_SIZE; i++){
            FreeBitmap[i] = (char)0;
        }

        for(int i = 0; i < INODE_TABLE_SIZE+ROOT_DIRECTORY_SIZE+1; i++){
            FreeBitmap[i] = (char)1;
        }

        for(int i = FILE_SYSTEM_SIZE-FREE_BITMAP_SIZE; i < FILE_SYSTEM_SIZE; i++){
            FreeBitmap[i] = (char)1;
        }
        
        //writes the free bitmap
        write_blocks(FILE_SYSTEM_SIZE-FREE_BITMAP_SIZE, FREE_BITMAP_SIZE, &FreeBitmap);
        
        //initializes the in-memory data structures
        initialize_descriptor_table();
        update_inode_table_cache();
        update_directory_cache();
    }else{
        //loads an existing disk
        init_disk(FILENAME, BLOCK_SIZE, FILE_SYSTEM_SIZE);

        //initializes the in-memory data structures
        initialize_descriptor_table();
        update_inode_table_cache();
        update_directory_cache();
    }
}

int sfs_getnextfilename(char *fname){
    //checks if there is a new filename to get
    if((*root_directory_cache).num_entries <= getnextfilename_index){
        //resets the index and returns 0 if there is no new filename to get
        getnextfilename_index = 0;
        return 0;
    }else{
        //gets the next filename if there is a new filename to get
        int count = 0;

        //finds the file by iterating through the cached directory entries
        for(int i = 0; i < FILE_SYSTEM_MAX_DIRECTORY_ENTRIES; i++){
            DirectoryEntry directory_entry = (*root_directory_cache).directory_entries[i];

            if(directory_entry.is_used == 1){
                if(count == getnextfilename_index){
                    strcpy(fname, directory_entry.filename);
                    getnextfilename_index++;
                    return 1;
                }

                count++;
            }
        }
    }

    return -1;
}

int sfs_getfilesize(const char* path){
    //finds the file by iterating through the cached directory entries
    for(int i = 0; i < FILE_SYSTEM_MAX_DIRECTORY_ENTRIES; i++){
        DirectoryEntry directory_entry = (*root_directory_cache).directory_entries[i];

        if(directory_entry.is_used == 1){
            const char *filename = directory_entry.filename;

            if(strcmp(path, filename) == 0){
                //finds the file's size by indexing into the file's inode
                int inode_index = directory_entry.inode;
                return (*inode_table_cache).inodes[inode_index].size;
            }
        }
    }

    return -1;
}

int sfs_fopen(char *name){
    //checks if the filename is within the maximum filename length
    if(strlen(name) > MAXFILENAME){
        return -1;
    }

    //checks if the file is already open
    for(int i = 0; i < FILE_SYSTEM_MAX_DIRECTORY_ENTRIES; i++){
        DescriptorTableEntry descriptor_table_entry = descriptor_table.descriptor_table_entries[i];
        if(descriptor_table_entry.is_used == 1){
            const char *filename = descriptor_table_entry.filename;
            
            if(strcmp(name, filename) == 0){
                //returns the file's descriptor table fileID if the file is already open
                return i;
            }
        }
    }

    //checks if the file is in the root directory if the file is not already open
    for(int i = 0; i < FILE_SYSTEM_MAX_DIRECTORY_ENTRIES; i++){
        DirectoryEntry directory_entry = (*root_directory_cache).directory_entries[i];

        if(directory_entry.is_used == 1){
            const char *filename = directory_entry.filename;
            
            if(strcmp(name, filename) == 0){
                //opens the file if the file is found in the root directory

                //creates the descriptor table entry for the file
                DescriptorTableEntry descriptor_table_entry;

                descriptor_table_entry.is_used = 1;
                descriptor_table_entry.inode = directory_entry.inode;
                descriptor_table_entry.filename = directory_entry.filename;
                descriptor_table_entry.read_pointer = 0;

                INode inode = (*inode_table_cache).inodes[directory_entry.inode];
                descriptor_table_entry.write_pointer = inode.size;

                //returns the file's descriptor table fileID
                int fileID = input_descriptor_table_entry(descriptor_table_entry);
                return fileID;
            }
        }
    }

    //creates the file if the file is not found in the root directory
    int block_index = allocate_free_block();

    //creates the inode for the new file
    INode new_file_inode;

    //file mode reference: https://www.gnu.org/software/libc/manual/html_node/Permission-Bits.html
    new_file_inode.is_used = 1;
    new_file_inode.mode = S_IFDIR | S_IRWXU | S_IRWXG | S_IRWXO;
    new_file_inode.link_count = 1;
    new_file_inode.size = 0;
    new_file_inode.ind_pointer = -1;

    new_file_inode.pointers[0] = block_index;

    for(int i = 1; i < INODE_SIZE; i++){
        new_file_inode.pointers[i] = -1;
    }

    int inode_index = allocate_free_inode(new_file_inode);

    //creates the directory entry for the new file
    DirectoryEntry new_file_directory_entry;

    new_file_directory_entry.is_used = 1;
    new_file_directory_entry.inode = inode_index;
    new_file_directory_entry.filename = name;

    input_directory_entry(new_file_directory_entry);

    //creates the descriptor table entry for the new file
    DescriptorTableEntry new_file_descriptor_table_entry;
    new_file_descriptor_table_entry.is_used = 1;
    new_file_descriptor_table_entry.inode = inode_index;
    new_file_descriptor_table_entry.filename = name;
    new_file_descriptor_table_entry.read_pointer = 0;
    new_file_descriptor_table_entry.write_pointer = 0;

    //returns the new file's descriptor table fileID
    int fileID = input_descriptor_table_entry(new_file_descriptor_table_entry);
    return fileID;
}

int sfs_fclose(int fileID){
    //finds the file by indexing into the descriptor table
    if(descriptor_table.descriptor_table_entries[fileID].is_used == 1){
        //replaces the file's descriptor table entry with an empty entry, thereby "closing" the file
        DescriptorTableEntry empty_entry;

        empty_entry.is_used = 0;
        empty_entry.inode = -1;
        empty_entry.filename = NULL;
        empty_entry.read_pointer = -1;
        empty_entry.write_pointer = -1;

        descriptor_table.descriptor_table_entries[fileID] = empty_entry;
        descriptor_table.num_entries--;

        //returns 0 if the file is successfully closed
        return 0;
    }

    return -1;
}

int sfs_frseek(int fileID, int loc){
    //finds the file by indexing into the descriptor table
    if(descriptor_table.descriptor_table_entries[fileID].is_used == 1){
        //updates the file's read pointer
        descriptor_table.descriptor_table_entries[fileID].read_pointer = loc;

        //returns 0 if the operation is successful
        return 0;
    }

    return -1;
}

int sfs_fwseek(int fileID, int loc){
    //finds the file by indexing into the descriptor table
    if(descriptor_table.descriptor_table_entries[fileID].is_used == 1){
        //updates the file's write pointer
        descriptor_table.descriptor_table_entries[fileID].write_pointer = loc;

        //returns 0 if the operation is successful
        return 0;
    }

    return -1;
}

int sfs_fwrite(int fileID, char *buf, int length){
    //finds the file by indexing into the descriptor table
    if(descriptor_table.descriptor_table_entries[fileID].is_used == 1){
        DescriptorTableEntry descriptor_table_entry = descriptor_table.descriptor_table_entries[fileID];

        INodeTable *inode_table_buffer = malloc(INODE_TABLE_SIZE*BLOCK_SIZE);
        read_blocks(1, INODE_TABLE_SIZE, inode_table_buffer);

        INode inode = (*inode_table_buffer).inodes[descriptor_table_entry.inode];

        //calculates the index of the start block pointer and the index of the end block pointer
        int start_index = descriptor_table_entry.write_pointer/BLOCK_SIZE;
        int end_index = (descriptor_table_entry.write_pointer+length-1)/BLOCK_SIZE;

        int bytes_to_write = length;
        int bytes_written = 0;
        
        //iterates over the blocks one at a time
        for(int i = start_index; i <= end_index; i++){
            int block_pointer;
            Indirect *indirect_buffer = NULL;

            //returns -1 if the index is out of the range of the inode's block pointers and the inode indirect's block pointers
            if(i >= (INDIRECT_MULTIPLIER+1)*INODE_SIZE){
                return -1;
            }
            
            //sets the block pointer to one of the inode's block pointers or one of the indirect's block pointers, depending on the index
            if(i >= INODE_SIZE){
                if((*inode_table_buffer).inodes[descriptor_table_entry.inode].ind_pointer == -1){
                    //creates the inode's indirect if needed
                    int indirect_index = allocate_free_block();
                    (*inode_table_buffer).inodes[descriptor_table_entry.inode].ind_pointer = indirect_index;

                    Indirect *new_indirect_buffer = malloc(BLOCK_SIZE);

                    for(int j = 0; j < INDIRECT_MULTIPLIER*INODE_SIZE; j++){
                        (*new_indirect_buffer).pointers[j] = -1;
                    }

                    write_blocks(indirect_index, 1, new_indirect_buffer);
                    free(new_indirect_buffer);
                }

                //gets the inode's indirect from the disk
                indirect_buffer = malloc(BLOCK_SIZE);
                read_blocks((*inode_table_buffer).inodes[descriptor_table_entry.inode].ind_pointer, 1, indirect_buffer);

                block_pointer = (*indirect_buffer).pointers[i-INODE_SIZE];
            }else{
                block_pointer = inode.pointers[i];
            }

            char *block_buffer = malloc(BLOCK_SIZE);

            if(block_pointer != -1){
                read_blocks(block_pointer, 1, block_buffer);
            }

            int local_pointer = descriptor_table_entry.write_pointer%BLOCK_SIZE;
            int local_size_to_write = BLOCK_SIZE-local_pointer;

            //writes the buffer into the block and updates the file's write pointer
            if(local_size_to_write <= bytes_to_write){
                memcpy(block_buffer+local_pointer, buf+bytes_written, local_size_to_write);
                bytes_written += local_size_to_write;
                bytes_to_write -= local_size_to_write;
                descriptor_table_entry.write_pointer += local_size_to_write;
            }else{
                memcpy(block_buffer+local_pointer, buf+bytes_written, bytes_to_write);
                bytes_written += bytes_to_write;
                descriptor_table_entry.write_pointer += bytes_to_write;
            }

            if(block_pointer != -1){
                //immediately writes to the block if it is already allocated to the file 
                write_blocks(block_pointer, 1, block_buffer);
            }else{
                //allocates a new block to the file if needed
                int new_block_index = allocate_free_block();

                if(new_block_index < 0){
                    return -1;
                }
                
                //updates the file's inode with the index of the newly allocated block
                if(i >= INODE_SIZE){
                    (*indirect_buffer).pointers[i-INODE_SIZE] = new_block_index;
                    write_blocks((*inode_table_buffer).inodes[descriptor_table_entry.inode].ind_pointer, 1, indirect_buffer);
                }else{
                    (*inode_table_buffer).inodes[descriptor_table_entry.inode].pointers[i] = new_block_index;
                }

                //writes to the newly allocated block
                write_blocks(new_block_index, 1, block_buffer);
            }
            
            free(block_buffer);

            if(indirect_buffer != NULL){
                free(indirect_buffer);
            }
        }

        //updates the size of the file
        (*inode_table_buffer).inodes[descriptor_table_entry.inode].size += length;
        
        //writes the updated inode table back onto the disk
        write_blocks(1, INODE_TABLE_SIZE, inode_table_buffer);
        update_inode_table_cache();

        //updates the file's descriptor table entry
        descriptor_table.descriptor_table_entries[fileID] = descriptor_table_entry;

        //returns the number of bytes written
        return bytes_written;
    }

    return -1;
}

int sfs_fread(int fileID, char *buf, int length){
    //finds the file by indexing into the descriptor table
    if(descriptor_table.descriptor_table_entries[fileID].is_used == 1){
        DescriptorTableEntry descriptor_table_entry = descriptor_table.descriptor_table_entries[fileID];

        INodeTable *inode_table_buffer = malloc(INODE_TABLE_SIZE*BLOCK_SIZE);
        read_blocks(1, INODE_TABLE_SIZE, inode_table_buffer);

        INode inode = (*inode_table_buffer).inodes[descriptor_table_entry.inode];

        //calculates the index of the start block pointer and the index of the end block pointer
        int start_index = descriptor_table_entry.read_pointer/BLOCK_SIZE;
        int end_index = (descriptor_table_entry.read_pointer+length-1)/BLOCK_SIZE;
        
        int bytes_to_read;

        if(length <= inode.size){
            bytes_to_read = length;
        }else{
            bytes_to_read = inode.size;
        }

        int bytes_read = 0;

        //iterates over the blocks one at a time
        for(int i = start_index; i <= end_index; i++){
            int block_pointer;
            Indirect *indirect_buffer = NULL;

            //returns -1 if the index is out of the range of the inode's block pointers and the inode indirect's block pointers
            if(i >= (INDIRECT_MULTIPLIER+1)*INODE_SIZE){
                return -1;
            }

            //sets the block pointer to one of the inode's block pointers or one of the indirect's block pointers, depending on the index
            if(i >= INODE_SIZE){
                //gets the inode's indirect from the disk
                indirect_buffer = malloc(BLOCK_SIZE);
                read_blocks((*inode_table_buffer).inodes[descriptor_table_entry.inode].ind_pointer, 1, indirect_buffer);

                block_pointer = (*indirect_buffer).pointers[i-INODE_SIZE];
            }else{
                block_pointer = inode.pointers[i];
            }

            //stops reading if there are no more blocks
            if(block_pointer == -1){
                break;
            }

            char *block_buffer = malloc(BLOCK_SIZE);
            read_blocks(block_pointer, 1, block_buffer);

            int local_pointer = descriptor_table_entry.read_pointer%BLOCK_SIZE;
            int local_size_to_read = BLOCK_SIZE-local_pointer;
            
            //reads the block into the buffer and updates the file's read pointer
            if(local_size_to_read <= bytes_to_read){
                memcpy(buf+bytes_read, block_buffer+local_pointer, local_size_to_read);
                bytes_read += local_size_to_read;
                bytes_to_read -= local_size_to_read;
                descriptor_table_entry.read_pointer += local_size_to_read;
            }else{
                memcpy(buf+bytes_read, block_buffer+local_pointer, bytes_to_read);
                bytes_read += bytes_to_read;
                descriptor_table_entry.read_pointer += bytes_to_read;
            }

            free(block_buffer);

            if(indirect_buffer != NULL){
                free(indirect_buffer);
            }
        }

        //updates the file's descriptor table entry
        descriptor_table.descriptor_table_entries[fileID] = descriptor_table_entry;

        //returns the number of bytes read
        return bytes_read;
    }

    return -1;
}

int sfs_remove(char *file){
    //returns -1 if the file is open and therefore cannot be removed right now
    for(int i = 0; i < FILE_SYSTEM_MAX_DIRECTORY_ENTRIES; i++){
        DescriptorTableEntry descriptor_table_entry = descriptor_table.descriptor_table_entries[i];
        if(descriptor_table_entry.is_used == 1){
            const char *descriptor_table_filename = descriptor_table_entry.filename;

            if(strcmp(file, descriptor_table_filename) == 0){
                return -1;
            }
        }
    }

    int inode_index;

    //finds the file by iterating through the cached directory entries
    for(int i = 0; i < FILE_SYSTEM_MAX_DIRECTORY_ENTRIES; i++){
        DirectoryEntry directory_entry = (*root_directory_cache).directory_entries[i];

        if(directory_entry.is_used == 1){
            const char *directory_filename = directory_entry.filename;

            if(strcmp(file, directory_filename) == 0){
                inode_index = directory_entry.inode;

                //replaces the file's directory entry with an empty entry
                DirectoryEntry empty_directory_entry;

                empty_directory_entry.is_used = 0;
                empty_directory_entry.inode = -1;
                empty_directory_entry.filename = "";

                Directory *root_directory_buffer = malloc(ROOT_DIRECTORY_SIZE*BLOCK_SIZE);
                int default_root_directory_block = INODE_TABLE_SIZE+1;
                read_blocks(default_root_directory_block, ROOT_DIRECTORY_SIZE, root_directory_buffer);

                (*root_directory_buffer).directory_entries[i] = empty_directory_entry;
                (*root_directory_buffer).num_entries--;

                //writes the updated root directory back onto the disk
                write_blocks(default_root_directory_block, ROOT_DIRECTORY_SIZE, root_directory_buffer);
                free(root_directory_buffer);
                update_directory_cache();

                //deallocates all of the file's data blocks
                INode inode = (*inode_table_cache).inodes[inode_index];

                for(int j = 0; j < INODE_SIZE; j++){
                    if(inode.pointers[j] != -1){
                        deallocate_block(inode.pointers[j]);
                    }
                }

                //deallocates all of the file's indirect data blocks and deallocates the indirect block itself
                if(inode.ind_pointer != -1){
                    Indirect *indirect_buffer = malloc(BLOCK_SIZE);
                    read_blocks(inode.ind_pointer, 1, indirect_buffer);

                    for(int j = 0; j < INDIRECT_MULTIPLIER*INODE_SIZE; j++){
                        if((*indirect_buffer).pointers[j] != -1){
                            deallocate_block((*indirect_buffer).pointers[j]);
                        }
                    }

                    free(indirect_buffer);
                    deallocate_block(inode.ind_pointer);
                }

                //replaces the file's inode table entry with an empty entry
                INode empty_inode_table_entry;

                //file mode reference: https://www.gnu.org/software/libc/manual/html_node/Permission-Bits.html
                empty_inode_table_entry.is_used = 0;
                empty_inode_table_entry.mode = S_IFDIR | S_IRWXU | S_IRWXG | S_IRWXO;
                empty_inode_table_entry.link_count = 0;
                empty_inode_table_entry.size = 0;
                empty_inode_table_entry.ind_pointer = -1;
                
                for(int j = 0; j < INODE_SIZE; j++){
                    empty_inode_table_entry.pointers[j] = -1;
                }

                INodeTable *inode_table_buffer = malloc(INODE_TABLE_SIZE*BLOCK_SIZE);
                read_blocks(1, INODE_TABLE_SIZE, inode_table_buffer);

                (*inode_table_buffer).inodes[inode_index] = empty_inode_table_entry;
                (*inode_table_buffer).num_entries--;

                //writes the updated inode table back onto the disk
                write_blocks(1, INODE_TABLE_SIZE, inode_table_buffer);
                free(inode_table_buffer);
                update_inode_table_cache();

                //returns 0 if the file was removed successfully
                return 0;
            }
        }
    }

    return -1;
}

int allocate_free_inode(INode new_inode){
    INodeTable *inode_table_buffer = malloc(INODE_TABLE_SIZE*BLOCK_SIZE);
    read_blocks(1, INODE_TABLE_SIZE, inode_table_buffer);

    //finds an available inode entry by iterating through the on-disk inode table
    for(int i = 0; i < FILE_SYSTEM_MAX_INODE_ENTRIES; i++){
        if((*inode_table_buffer).inodes[i].is_used == 0){
            //sets the new inode
            (*inode_table_buffer).inodes[i] = new_inode;
            (*inode_table_buffer).num_entries++;

            //writes the updated inode table back onto the disk
            write_blocks(1, INODE_TABLE_SIZE, inode_table_buffer);
            free(inode_table_buffer);
            update_inode_table_cache();

            //returns the index of the new inode in the inode table
            return i;
        }
    }

    free(inode_table_buffer);
    return -1;
}

int input_directory_entry(DirectoryEntry new_entry){
    Directory *root_directory_buffer = malloc(ROOT_DIRECTORY_SIZE*BLOCK_SIZE);
    int default_root_directory_block = INODE_TABLE_SIZE+1;
    read_blocks(default_root_directory_block, ROOT_DIRECTORY_SIZE, root_directory_buffer);

    //finds an available directory entry by iterating through the on-disk root directory
    for(int i = 0; i < FILE_SYSTEM_MAX_DIRECTORY_ENTRIES; i++){
        if((*root_directory_buffer).directory_entries[i].is_used == 0){
            //sets the new directory entry
            (*root_directory_buffer).directory_entries[i] = new_entry;
            (*root_directory_buffer).num_entries++;

            //writes the updated root directory back onto the disk
            write_blocks(default_root_directory_block, ROOT_DIRECTORY_SIZE, root_directory_buffer);
            free(root_directory_buffer);
            update_directory_cache();

            //returns 0 if the operation is successful
            return 0;
        }
    }

    free(root_directory_buffer);
    return -1;
}

int input_descriptor_table_entry(DescriptorTableEntry new_entry){
    //finds an available descriptor table entry by iterating through the in-memory descriptor table
    for(int i = 0; i < FILE_SYSTEM_MAX_DIRECTORY_ENTRIES; i++){
        if(descriptor_table.descriptor_table_entries[i].is_used == 0){
            //sets the descriptor table entry
            descriptor_table.descriptor_table_entries[i] = new_entry;
            descriptor_table.num_entries++;

            //returns the index of the descriptor table entry in the descriptor table
            return i;
        }
    }

    return -1;
}

int allocate_free_block(){
    read_blocks(FILE_SYSTEM_SIZE-FREE_BITMAP_SIZE, FREE_BITMAP_SIZE, &FreeBitmap);
    int default_root_directory_block = INODE_TABLE_SIZE+1;

    //finds an available data block by iterating through the on-disk free bitmap
    for(int i = default_root_directory_block; i < FILE_SYSTEM_SIZE-FREE_BITMAP_SIZE-1; i++){
        if(FreeBitmap[i] == (char)0){
            //sets the availability of the selected block to taken
            FreeBitmap[i] = (char)1;

            //writes the updated free bitmap back onto the disk
            write_blocks(FILE_SYSTEM_SIZE-FREE_BITMAP_SIZE, FREE_BITMAP_SIZE, &FreeBitmap);

            //returns the index of the selected data block
            return i;
        }
    }

    return -1;
}

void deallocate_block(int block_index){
    read_blocks(FILE_SYSTEM_SIZE-FREE_BITMAP_SIZE, FREE_BITMAP_SIZE, &FreeBitmap);

    //writes an empty block to the disk in order to reset the block
    char *empty_buffer = malloc(BLOCK_SIZE);
    write_blocks(block_index, 1, empty_buffer);
    free(empty_buffer);

    //sets the availability of the selected block to available
    FreeBitmap[block_index] = (char)0;

    //writes the updated free bitmap back onto the disk
    write_blocks(FILE_SYSTEM_SIZE-FREE_BITMAP_SIZE, FREE_BITMAP_SIZE, &FreeBitmap);
}

void initialize_descriptor_table(){
    //initializes the in-memory descriptor table with empty descriptor table entries
    descriptor_table.num_entries = 0;

    for(int i = 0; i < FILE_SYSTEM_MAX_DIRECTORY_ENTRIES; i++){
        DescriptorTableEntry empty_entry;

        empty_entry.is_used = 0;
        empty_entry.inode = -1;
        empty_entry.filename = NULL;
        empty_entry.read_pointer = -1;
        empty_entry.write_pointer = -1;

        descriptor_table.descriptor_table_entries[i] = empty_entry;
    }
}

void update_inode_table_cache(){
    //loads the inode table on the disk into the in-memory inode table cache
    INodeTable *inode_table_buffer = malloc(INODE_TABLE_SIZE*BLOCK_SIZE);
    read_blocks(1, INODE_TABLE_SIZE, inode_table_buffer);

    if(inode_table_cache == NULL){
        inode_table_cache = inode_table_buffer;
    }else{
        *inode_table_cache = *inode_table_buffer;
        free(inode_table_buffer);
    }
}

void update_directory_cache(){
    //loads the root directory on the disk into the in-memory root directory cache
    Directory *root_directory_buffer = malloc(ROOT_DIRECTORY_SIZE*BLOCK_SIZE);
    int default_root_directory_block = INODE_TABLE_SIZE+1;
    read_blocks(default_root_directory_block, ROOT_DIRECTORY_SIZE, root_directory_buffer);

    if(root_directory_cache == NULL){
        root_directory_cache = root_directory_buffer;
    }else{
        *root_directory_cache = *root_directory_buffer;
        free(root_directory_buffer);
    }
}