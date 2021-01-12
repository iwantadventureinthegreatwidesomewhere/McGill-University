#ifndef PREFERENCES_H_
#define PREFERENCES_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <stddef.h>

#ifdef DEBUG
	#define debug(...) printf(__VA_ARGS__)
#else
	#define debug(...)
#endif 

typedef struct s_preference_info{ 
	char  first_name[100];
	char last_name[100]; 
	double favorite_number;
	double estimated_btc_price;
	int cats_or_dogs;
	char first_language[100];
	char last_language[100];
	char favorite_meal[100];
	char favorite_book[100];
	int year_of_singularity;
	char last_book[100];
	char favorite_dessert[100];
	char favorite_movie[100];
} PREFERENCE_INFO;

#define MAX_LINE_LENGTH 2000
#define MAX_FIELD_LENGTH 500
#define MAX_LINES 20

void print_preference( PREFERENCE_INFO *pref );
void load_one_preference( PREFERENCE_INFO *dest, char *input_file );

#endif