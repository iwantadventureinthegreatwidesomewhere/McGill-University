#include "preferences.h"
enum field_list{  first_name, last_name, favorite_number, estimated_btc_price, cats_or_dogs, 
                  first_language, last_language, favorite_meal, favorite_book, year_of_singularity, 
                  last_book, favorite_dessert,  favorite_movie, NUM_FIELDS };

void input_string( void *string_field, char *input_text ){ strcpy( (char*)string_field, input_text ); }
void input_double( void *number_field, char *input_text ){ *(double*)number_field = atof(input_text); }
void input_animal( void *animal_field, char *input_text ){ 
	if( input_text[0] == 'c' || input_text[0] == 'C' )
		*(int*)animal_field = 0;
	else if( input_text[0] == 'd' || input_text[0] == 'D' )
		*(int*)animal_field = 1;
	else
		*(int*)animal_field = 2;
}
void input_int( void *number_field, char *input_text ){ *(int*)number_field = atof(input_text); }
void (*input_fcn_array[NUM_FIELDS])(void*, char*)= { input_string, input_string, input_double, input_double, input_animal, input_string, input_string, input_string, input_string, input_int, input_string, input_string, input_string };

void output_string( char *output_text, void *string_field ){ sprintf( output_text, "%s", (char*)string_field ); }
void output_double( char *output_text, void *number_field ){ sprintf( output_text, "%g", *(double*)number_field ); }
void output_animal( char *output_text, void *animal_field ){ 
	if( *(int*)animal_field == 0 )
		sprintf( output_text, "%s", "Catlike" );
	else if( *(int*)animal_field == 1 ) 
		sprintf( output_text, "%s", "Doglike" );
	else	
		sprintf( output_text, "%s", "Other" );
}
void output_int( char *output_text, void *number_field ){ sprintf( output_text, "%d", *(int*)number_field ); }
void (*output_fcn_array[NUM_FIELDS])(char*, void*)= { output_string, output_string, output_double, output_double, output_animal, output_string, output_string, output_string, output_string, output_int, output_string, output_string, output_string };

int field_offsets[NUM_FIELDS] = { 
offsetof( struct s_preference_info, first_name ),
offsetof( struct s_preference_info, last_name ),
offsetof( struct s_preference_info, favorite_number ),
offsetof( struct s_preference_info, estimated_btc_price ),
offsetof( struct s_preference_info, cats_or_dogs ),
offsetof( struct s_preference_info, first_language ),
offsetof( struct s_preference_info, last_language ),
offsetof( struct s_preference_info, favorite_meal ),
offsetof( struct s_preference_info, favorite_book ),
offsetof( struct s_preference_info, year_of_singularity ),
offsetof( struct s_preference_info, last_book ),
offsetof( struct s_preference_info, favorite_dessert ),
offsetof( struct s_preference_info, favorite_movie )           };	

#define LOAD_FIELD( dest, src, field_number ) input_fcn_array[field_number]( (void*)((char*)(dest)+field_offsets[field_number]), src )
#define PRINT_FIELD( dest, src, field_number ) output_fcn_array[field_number]( dest, (void*)((char*)(src)+field_offsets[field_number]) )

void load_one_preference( PREFERENCE_INFO *dest, char *input_file ){

	int field_number = 0;
	char lines[MAX_LINES][MAX_LINE_LENGTH];
	int this_line_num = 0;
	int max_length = -1;
	int longest;
	int first_comment=0;

	char buffer[MAX_LINE_LENGTH];
	memset( buffer, '\0', MAX_LINE_LENGTH );
	FILE *fp = fopen( input_file, "r" );
	int ret = fread( buffer, 1, MAX_LINE_LENGTH, fp );
	fclose(fp);	
	buffer[ret] = '\0';
	char *input_data = buffer;

	while( *input_data != '\0' ){

		memset( lines[this_line_num], '\0', MAX_LINE_LENGTH );

		if( *input_data == '#' && first_comment==0 ){
			while( *(input_data) != '\n' && *(input_data) != '\0')
				input_data++; 
			first_comment=1;
		}
		
		char *line_ptr = lines[this_line_num];
		while( *input_data != '\n' && *input_data != '\0' )
			*(line_ptr++) = *(input_data++);

		if( *input_data == '\n' )
			input_data++;

		*(line_ptr++) = '\0';

		char *header = strstr(lines[this_line_num], "First Name");
		if( line_ptr - lines[this_line_num] > max_length && !header){
			max_length = line_ptr - lines[this_line_num];
			longest = this_line_num;
		}

		this_line_num++;
		if( this_line_num == MAX_LINES)
			break;
	}

	input_data = lines[longest];

	while( *input_data != '\0' ){

		while( *input_data == ' ' || *input_data == '\t' || *input_data == '#' )
			input_data++; 
		char src[MAX_FIELD_LENGTH];
		char *pos = src;
		while( *input_data != ',' && *input_data != '\0' && *input_data != '\n' )
			*(pos++) = *(input_data++);
		if( *input_data == ',' ) 
			input_data++;
		*pos = '\0';

		LOAD_FIELD( dest, src, field_number );

		field_number++;
		if( field_number == NUM_FIELDS ) 
			break;
	}
}

void print_preference( PREFERENCE_INFO *pref ){

	char field_text[MAX_FIELD_LENGTH];
	for( int field_number=0; field_number<NUM_FIELDS; field_number++ ){
		PRINT_FIELD( field_text, pref, field_number );
		printf( "%s", field_text );
		if( field_number < NUM_FIELDS-1 )
			printf(", ");
		else
			printf("\n");
	}
}