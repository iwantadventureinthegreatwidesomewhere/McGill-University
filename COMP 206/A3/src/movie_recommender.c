#include "preferences.h"
#include "distances.h"

int main( int argc, char* argv[] ){

	if( argc < 3 ){
		printf( "Usage: %s target_user_preferences <list_of_other_user_preferences>\n", argv[0] );
		return -1;	
	}

	PREFERENCE_INFO class_preferences[argc-2];
	PREFERENCE_INFO target_user;
	
	load_one_preference( &target_user, argv[1] );

	debug( "Target user has preferences:\n");
	#ifdef DEBUG
		print_preference( &target_user );
	#endif
	
	double min_distance = 9999999.0;
	double this_distance;
	char best_fname[100]; 
	PREFERENCE_INFO *best_match = NULL;

	for( int student=2; student<argc; student++ )
	{
		debug( "Loading data from file: %s.\n", argv[student]);
		load_one_preference( class_preferences+student-2, argv[student] );

		#ifdef DEBUG
			print_preference( class_preferences+student-2 );
		#endif

		this_distance = compute_difference_numeric( &target_user, class_preferences+student-2 ) + compute_difference_alphabetic( &target_user, class_preferences+student-2 );

		if( this_distance < min_distance ){
			min_distance = this_distance;
			best_match = class_preferences+student-2;
			strcpy( best_fname, argv[student] );
		}
	}

	debug( "Best match from user list was from file %s with distance %f: ", best_fname, min_distance );
	#ifdef DEBUG
		print_preference( best_match );
	#endif

	printf( "Top movie pick for user %s %s is: %s.\n", target_user.first_name, target_user.last_name, best_match->favorite_movie );
	
	return 0;	
}
