#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include<signal.h>
#include <sys/wait.h>
#include <sys/time.h> 
#include <sys/resource.h>
#include <fcntl.h>

//global variables
char* history[100];
int historyCounter = 0;
long limit = -1;
char* fifoPath = NULL;
int terminate = 0;

//signal handler for Ctrl+C
void handle_sigint(int sig) 
{
    char resp[1000];

    printf("Do you want to terminate the running process? (y/n)\n");
    scanf("%s", resp);
    rewind(stdin);

    /**
     * exits process if resp is 'y' for yes or
     * returns to the process if resp is 'n' for no,
     * otherwise calls this handler to ask for confirmation again
     **/
    if(strcmp(resp, "y") == 0){
        exit(0);
    }else if(strcmp(resp, "n") == 0){
        return;
    }else{
        handle_sigint(sig);
    }
}

char* get_a_line(){
    char* buffer = (char*) malloc(sizeof(char) * 1000);
    int counter = 0;

    //iterates the characters of a single line
    while(1){
        int i = getc(stdin);
        char c = (char) i;

        /**
         * puts the character 'c' in the line buffer 'buffer' if the
         * character is not '\n' or EOF, otherwise returns the line buffer
         **/
        if(c != '\n' && i != -1){
            *(buffer + counter) = c;
            counter++;
        }else{
            /**
             * sets the 'terminate' variable to 1 tiny shell detects the end-of-file 
             * condition (when running non-interactively) so that my_system exits 
             * the shell after executing the line
             **/
            if(i == -1){
                terminate = 1;
            }

            return buffer;
        }
    }
}

int my_system(char* line){
    //adds the most recent line to the history buffer
    history[historyCounter%100] = line;
    historyCounter++;
    
    //splits the line into program and program arguments
    char* program;
    char* arguments[1000];
    int counter = 0;

    char* copy = (char*) malloc(strlen(line) + 1); 
    strcpy(copy, line);

    char* ptr = strtok(copy, " ");
    program = ptr;

    arguments[counter] = ptr;
    counter++;

    while (ptr != NULL)
    {
        ptr = strtok(NULL, " ");
        arguments[counter] = ptr;
        counter++;
    }

    //checks if the line contains the pipe character
    int executeFifo = 0;
    int split = -1;

    for(int i = 0; i < 1000; i++){
        if(arguments[i] == NULL){
            break;
        }

        if(strcmp(arguments[i], "|") == 0){
            executeFifo = 1;
            split = i;
        }
    }

    /**
     * if the line contains the pipe character '|', the shell will use the named
     * pipe at 'fifoPath' to run the line's command
     **/
    if(executeFifo){
        //checks if a FIFO file path was passed to the shell
        if(fifoPath != NULL){
            //splits the line into its two separate commands 'first' (writing) and 'second' (reading)
            char* first[1000];

            for(int i = 0; i < split; i++){
                first[i] = arguments[i];
            }

            char* second[1000];

            for(int i = 0; i < 1000-split-1; i++){
                second[i] = arguments[split+1+i];
            }

            int fd;

            //creates a child process for the first command
            pid_t pid1 = fork();

            signal(SIGINT, SIG_IGN);
            signal(SIGTSTP, SIG_IGN);

            if(pid1 == 0){
                signal(SIGINT, handle_sigint);
                signal(SIGTSTP, SIG_IGN);
                
                /**
                 * sets the upper limit for the allowed resource usage for the child 
                 * process if a limit was previously set
                 **/
                if(limit != -1){
                    struct rlimit limits;
                    limits.rlim_cur = limit;
                    limits.rlim_max = limit;

                    setrlimit(RLIMIT_DATA, &limits);
                }

                //closes the default stdout stream
                close(1);
                //opens FIFO (writing only)
                fd = open(fifoPath, O_WRONLY);
                //duplicates the file descriptor 'fd', so that the FIFO is stdout
                dup(fd);

                //executes the program with its arguments
                execvp(first[0], first);

                close(fd);
            }else{
                signal(SIGINT, handle_sigint);
                signal(SIGTSTP, SIG_IGN);

                //creates a child process for the second command
                pid_t pid2 = fork();

                signal(SIGINT, SIG_IGN);
                signal(SIGTSTP, SIG_IGN);

                if(pid2 == 0){
                    signal(SIGINT, handle_sigint);
                    signal(SIGTSTP, SIG_IGN);

                    /**
                     * sets the upper limit for the allowed resource usage for the child 
                     * process if a limit was previously set
                     **/
                    if(limit != -1){
                        struct rlimit limits;
                        limits.rlim_cur = limit;
                        limits.rlim_max = limit;

                        setrlimit(RLIMIT_DATA, &limits);
                    }

                    //closes the default stdin stream
                    close(0);
                    //opens FIFO (reading only)
                    fd = open(fifoPath, O_RDONLY);
                    //duplicates the file descriptor 'fd', so that the FIFO is stdin
                    dup(fd);
                    
                    //waits for the child process of the first command to finish
                    int status;
                    wait(&status);

                    //executes the program with its arguments
                    execvp(second[0], second);

                    close(fd);
                }else{
                    //waits for the child process of the second command to finish
                    int status;
                    wait(&status);

                    signal(SIGINT, handle_sigint);
                    signal(SIGTSTP, SIG_IGN);
                }
            }
        }else{
            printf("No FIFO file path was passed to the running process.\n");
        }
    }else if(strcmp(program, "history") == 0){
        /**
         * prints the history starting at historyCounter+i so that the order
         * of the history is preserved
         **/
        int orderedHistoryCounter = 1;

        for(int i = 0; i < 100; i++){
            if(history[(historyCounter+i)%100] != NULL){
                printf("%d  %s\n", orderedHistoryCounter, history[(historyCounter+i)%100]);
                orderedHistoryCounter++;
            }
        }
    }else if(strcmp(program, "chdir") == 0){
        /**
         * uses getenv() to change the directory of the shell to the
         * argument, or to the home directory if no argument was passed
         **/
        if(arguments[1] == NULL){
            if (chdir(getenv("HOME")) != 0){
                fprintf(stderr, "chdir: %s: ", getenv("HOME"));
                perror("");
            }
        }else{
            if (chdir(arguments[1]) != 0){
                fprintf(stderr, "chdir: %s: ", arguments[1]);
                perror("");
            }
        }
    }else if(strcmp(program, "limit") == 0){
        /**
         * sets the upper limit for the allowed resource usage for future processes if
         * the limit is valid
         **/
        char *ptr;
        long l = strtol(arguments[1], &ptr, 10);

        if(l != 0){
            limit = l;
        }else{
            printf("limit: Not a valid limit\n");
        }
    }else{
        //creates a child process for the command
        pid_t pid = fork();

        signal(SIGINT, SIG_IGN);
        signal(SIGTSTP, SIG_IGN);

        if(pid == 0){
            signal(SIGINT, handle_sigint);
            signal(SIGTSTP, SIG_IGN);

            /**
             * sets the upper limit for the allowed resource usage for the child 
             * process if a limit was previously set
             **/
            if(limit != -1){
                struct rlimit limits;
                limits.rlim_cur = limit;
                limits.rlim_max = limit;

                setrlimit(RLIMIT_DATA, &limits);
            }

            //executes the program with its arguments
            execvp(program, arguments);
        }else{
            //waits for the child process of the command to finish
            int status;
            wait(&status);

            signal(SIGINT, handle_sigint);
            signal(SIGTSTP, SIG_IGN);
        }
    }

    //terminates the shell due to the detection of the end-of-file condition
    if(terminate == 1){
        exit(0);
    }

    return 0;
}

int main(int argc, char* argv[]){
    /**
     * changes how the Ctrl+C and Ctrl+Z signals are
     * handled by the shell: Ctrl+C will be handled by the
     * handle_sigint() function and Ctrl+Z will simply be ignored
     **/
    signal(SIGINT, handle_sigint);
    signal(SIGTSTP, SIG_IGN);

    //sets 'fifoPath' if a FIFO file path was passed to the shell
    if(argc == 2){
        fifoPath = argv[1];
    }
    
    while(1){
        char* line = get_a_line();
        if(strlen(line) > 1){
            my_system(line);
        }  
    }
}
