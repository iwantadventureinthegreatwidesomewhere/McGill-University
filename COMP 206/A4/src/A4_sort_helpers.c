#include "A4_sort_helpers.h"
sem_t* sem_array[27];
char* temp = "temp.txt";

// Function: read_all() 
// Provided to read an entire file, line by line.
// No need to change this one.
void read_all( char *filename ){
    
    FILE *fp = fopen( filename, "r" );
    int curr_line = 0;
	
    while( curr_line < MAX_NUMBER_LINES && 
           fgets( text_array[curr_line], MAX_LINE_LENGTH, fp ) )
    {
        curr_line++;
    }
	
    text_array[curr_line][0] = '\0';
    fclose(fp);
}

// Function: read_all() 
// Provided to read only the lines of a file staring with first_letter.
// No need to change this one.
void read_by_letter( char *filename, char first_letter ){

    FILE *fp = fopen( filename, "r" );
    int curr_line = 0;
    text_array[curr_line][0] = '\0';
	
    while( fgets( text_array[curr_line], MAX_LINE_LENGTH, fp ) ){
        if( text_array[curr_line][0] == first_letter ){
            curr_line++;
        }

        if( curr_line == MAX_NUMBER_LINES ){
            sprintf( buf, "ERROR: Attempted to read too many lines from file.\n" );
            write( 1, buf, strlen(buf) );
            break;
        }
    }
	
    text_array[curr_line][0] = '\0';
    fclose(fp);
}

// YOU COMPLETE THIS ENTIRE FUNCTION FOR Q1.
void sort_words( ){
    int i = 0;
    for(int i = 0; i < MAX_NUMBER_LINES; i++){
        if(text_array[i][0] == '\0'){
            break;
        }
        for(int j = i; j < MAX_NUMBER_LINES; j++){
            if(text_array[j][0] == '\0'){
                break;
            }
            if(strcmp(text_array[i],text_array[j]) > 0){
                char temp[MAX_LINE_LENGTH];
                for(int k = 0; k < MAX_LINE_LENGTH; k++){
                    temp[k] = text_array[i][k];
                    text_array[i][k] = text_array[j][k];
                    text_array[j][k] = temp[k];
                }
            }
        }
    }
}

// YOU COMPLETE THIS ENTIRE FUNCTION FOR Q2.
int initialize( ){
    for(int i = 0; i < 26; i++){
        sprintf(buf, "sem_%c", (char)('a'+i));
        sem_unlink(buf);
        sem_array[i] = sem_open(buf, O_CREAT, 0666, 0);
    }
    sem_unlink("parent");
    sem_array[26] = sem_open("parent", O_CREAT, 0666, 0);
    sem_post(sem_array[0]);
    return 0;
}

// YOU MUST COMPLETE THIS FUNCTION FOR Q2 and Q3.
int process_by_letter( char* input_filename, char first_letter ){
    sem_wait(sem_array[first_letter-97]);
    read_by_letter( input_filename, first_letter );
    sort_words( );
    
    FILE* fp = fopen(temp, "a");
    int curr_line = 0;
    while(text_array[curr_line][0] != '\0'){
        fprintf(fp, "%s",  text_array[curr_line]);
        curr_line++;
    }
    if(first_letter == 'z'){
        fprintf(fp, "Sorting complete!\n");
    }
    fclose(fp);
    sem_post(sem_array[first_letter-96]);
    return 0;
}

// YOU COMPLETE THIS ENTIRE FUNCTION FOR Q2 and Q3.
int finalize( ){
    sem_wait(sem_array[26]);
    read_all(temp);
    int curr_line = 0;
    while( text_array[curr_line][0] != '\0' ){
        printf("%s",  text_array[curr_line]);
        curr_line++;
    }
    return 0;
}

