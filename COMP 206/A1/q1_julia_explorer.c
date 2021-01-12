/*
 * q1_julia_explorer.c
 *
 *  Created on: Feb 7, 2018
 *      Author: marcoguida
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

int main(int argc, char* argv[]){

	if (argc != 4){
		printf("Error: 3 input args required");
		return -1;
	}

	if (access(argv[1], F_OK) == -1){
		printf("Error: bad file\n");
		return -1;
	}


	float a = atof(argv[2]);
	float b = atof(argv[3]);
	
	if (a == 0.0 || b == 0.0){
		printf("Error: bad float arg\n");
		return -1;
	}

	
	FILE * fp = fopen(argv[1], "r");

	char string[1000];

	while (!feof(fp)){
		fgets(string, sizeof string, fp);
		if(strstr(string, "#A#")){
			printf("a= %.6f\n", a);
		} else if(strstr(string, "#B#")){
			printf("b= %.6f\n", b);
		} else {
			printf("%s", string);
		}
	}

	fclose(fp);
	return 0;
}
