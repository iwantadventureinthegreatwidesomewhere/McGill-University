#include <sys/stat.h>

#define FILENAME  "sfs.sfs"

#define BLOCK_SIZE  1024
#define FILE_SYSTEM_SIZE  2048

#define INODE_SIZE 12
#define INODE_TABLE_SIZE 18
#define FILE_SYSTEM_MAX_INODE_ENTRIES 256

#define FILE_SYSTEM_MAX_DIRECTORY_ENTRIES 256

#define ROOT_DIRECTORY_SIZE 5

#define FREE_BITMAP_SIZE 2

#define MAXFILENAME 20

#define INDIRECT_MULTIPLIER 2

typedef struct{ 
    int magic;
    int block_size;
    int file_system_size;
    int inode_table_entries;
    int root_directory_inode;
} SuperBlock;

typedef struct{
    int is_used;
    mode_t mode;
    int link_count;
    int size;
    int pointers[INODE_SIZE]; 
    int ind_pointer;
} INode;

typedef struct{
    int pointers[INDIRECT_MULTIPLIER*INODE_SIZE];
} Indirect;

typedef struct{
    int num_entries;
    INode inodes[FILE_SYSTEM_MAX_INODE_ENTRIES];
} INodeTable;

typedef struct{
    int is_used;
    int inode;
    char *filename;
} DirectoryEntry;

typedef struct{
    int num_entries;
    DirectoryEntry directory_entries[FILE_SYSTEM_MAX_DIRECTORY_ENTRIES];
} Directory;

typedef struct{
    int is_used;
    int inode;
    char *filename;
    int read_pointer;
    int write_pointer;
} DescriptorTableEntry;

typedef struct{
    int num_entries;
    DescriptorTableEntry descriptor_table_entries[FILE_SYSTEM_MAX_DIRECTORY_ENTRIES];
} DescriptorTable;

void mksfs(int fresh);
int sfs_getnextfilename(char *fname);
int sfs_getfilesize(const char* path);
int sfs_fopen(char *name);
int sfs_fclose(int fileID);
int sfs_frseek(int fileID, int loc);
int sfs_fwseek(int fileID, int loc);
int sfs_fwrite(int fileID, char *buf, int length); 
int sfs_fread(int fileID, char *buf, int length); 
int sfs_remove(char *file);
int allocate_free_inode(INode new_inode);
int input_directory_entry(DirectoryEntry new_entry);
int input_descriptor_table_entry(DescriptorTableEntry new_entry);
int allocate_free_block();
void deallocate_block(int block_index);
void initialize_descriptor_table();
void update_inode_table_cache();
void update_directory_cache();