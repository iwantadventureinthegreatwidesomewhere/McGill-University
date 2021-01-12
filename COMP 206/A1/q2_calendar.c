/*
 * 2.c
 *
 *  Created on: Feb 8, 2018
 *      Author: marcoguida
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

void printDash (int length){
	printf("|");
	int count = 0;
	while(count < length-2){
		printf("-");
		count++;
	}
	printf("|\n");
}

void printMonthLabel(int length, char* string){
	int l = length-2;
	printf("|");
	int i;
	for(i = 0; string[i] != '\0'; ++i);
	int count = 0;
	while(count <= l-i){
		if(count == ((l/2)-(i/2))){
			printf("%s",string);
		} else {
			printf(" ");
		}
		count++;
	}
	printf("|\n");
}

void printDayLabel(int length, int daysize){
	int l = length - 8;

	char daysOfWeek[7][100] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

	printf("|");

	int countDay = 0;

	while (countDay < 7){
		printf(" ");
		char* day = daysOfWeek[countDay];
		int countChar = 0;

		while(countChar <  ((l/7)-1)){
			int i;
			for(i = 0; day[i] != '\0'; ++i);

			if(countChar < i && countChar <  ((l/7)-2)){
				printf("%c", day[countChar]);
			} else {
				printf(" ");
			}
			countChar++;
		}
		printf("|");
		countDay++;
	}
	printf("\n");
}

void printDays(int firstDay, int length){
	int l = length-8;
	int count = 0;
	int countDay = 1;
	printf("|");
	//first week//
	while(count < firstDay-1){
		int countSpace = 0;
		while(countSpace < l/7){
			printf(" ");
			countSpace++;

		}
		printf("|");
		count++;
	}
	while (count<7){
		printf(" ");
		int countChar = 0;
		if (countDay < 10){
			printf("%d", countDay);
			while(countChar <  ((l/7)-2)){
				printf(" ");
				countChar++;
			}
		}
		if (countDay >= 10){
					printf("%d", countDay);
					while(countChar <  ((l/7)-3)){
						printf(" ");
						countChar++;
					}
				}
		printf("|");
		countDay++;
		count++;

	}
	printf("\n");
	//second week
	printf("|");

	while (count<14){
			printf(" ");
			int countChar = 0;
			if (countDay < 10){
				printf("%d", countDay);
				while(countChar <  ((l/7)-2)){
					printf(" ");
					countChar++;
				}
			}
			if (countDay >= 10){
						printf("%d", countDay);
						while(countChar <  ((l/7)-3)){
							printf(" ");
							countChar++;
						}
					}
			printf("|");
			countDay++;
			count++;

		}
	printf("\n");
	//third week
	printf("|");
	while (count<21){
			printf(" ");
			int countChar = 0;
			if (countDay < 10){
				printf("%d", countDay);
				while(countChar <  ((l/7)-2)){
					printf(" ");
					countChar++;
				}
			}
			if (countDay >= 10){
						printf("%d", countDay);
						while(countChar <  ((l/7)-3)){
							printf(" ");
							countChar++;
						}
					}
			printf("|");
			countDay++;
			count++;

		}
	printf("\n");
	//fourth week
	printf("|");
	while (count<28){
			printf(" ");
			int countChar = 0;
			if (countDay < 10){
				printf("%d", countDay);
				while(countChar <  ((l/7)-2)){
					printf(" ");
					countChar++;
				}
			}
			if (countDay >= 10){
						printf("%d", countDay);
						while(countChar <  ((l/7)-3)){
							printf(" ");
							countChar++;
						}
					}
			printf("|");
			countDay++;
			count++;

		}
	printf("\n");
	//fifth week
	printf("|");
	while (count<35){
		if(countDay < 31){
			printf(" ");
			int countChar = 0;
			if (countDay < 10){
				printf("%d", countDay);
				while(countChar <  ((l/7)-2)){
					printf(" ");
					countChar++;
				}
			}
			if (countDay >= 10){
				printf("%d", countDay);
					while(countChar <  ((l/7)-3)){
						printf(" ");
						countChar++;
					}
			}
			printf("|");
			countDay++;
			count++;
		} else {
			int countSpace = 0;
				while(countSpace < l/7){
					printf(" ");
					countSpace++;

				}
			printf("|");
			count++;
		}
	}
	printf("\n");
	//optinial sixth week

	if (countDay < 31){
		printf("|");
		while (count<42){
			if(countDay < 31){
				printf(" ");
				int countChar = 0;
				if (countDay < 10){
					printf("%d", countDay);
					while(countChar <  ((l/7)-2)){
						printf(" ");
						countChar++;
					}
				}
				if (countDay >= 10){
					printf("%d", countDay);
						while(countChar <  ((l/7)-3)){
							printf(" ");
							countChar++;
						}
				}
				printf("|");
				countDay++;
				count++;
			} else {
				int countSpace = 0;
					while(countSpace < l/7){
						printf(" ");
						countSpace++;

					}
				printf("|");
				count++;
			}
		}
		printf("\n");
	}
}

void printMonth(int length, char* string, int firstDay, int daysize){
	printDash(length);
	printMonthLabel(length, string);
	printDash(length);
	printDayLabel(length, daysize);
	printDash(length);
	printDays(firstDay, length);
}



int main (int argc, char* argv[]){
	int daySize = atoi(argv[1]);
	if (daySize < 2){
		printf("Error: day size (first arg) is not greater than or equal to 2.\n");
		return -1;
	}
	int firstDay = atoi(argv[2]);
		if(1 > firstDay || firstDay > 7){
			printf("Error: first day (second arg) is not in [1, 7].\n");
			return -1;
		}

	int length = ((daySize+3)*7)+1;

	char daysOfWeek[12][100] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	int countMonth = 0;
	while(countMonth < 12){
		if(firstDay > 7){
			firstDay = 0 + firstDay%7;
		}
		printMonth(length, daysOfWeek[countMonth], firstDay, daySize);
		firstDay = firstDay+2;
		countMonth++;
	}
	printDash(length);
}
