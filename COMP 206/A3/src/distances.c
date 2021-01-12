#include "distances.h"
#include <math.h>
double compute_difference_numeric( PREFERENCE_INFO *one, PREFERENCE_INFO *two ){

	return 1/10.0 *    fabs(one->favorite_number - two->favorite_number) + 
	       1/10000.0 * fabs( one->estimated_btc_price - two->estimated_btc_price) + 
		               fabs( one->cats_or_dogs - two->cats_or_dogs) + 
		   1/10.0 *    fabs( one->year_of_singularity - two->year_of_singularity);
}

double compute_difference_alphabetic( PREFERENCE_INFO *one, PREFERENCE_INFO *two ){

	int dist = 0;
	dist += abs( one->first_language[0] -  two->first_language[0] );
	dist += abs( one->last_language[0] - two->last_language[0] );
	dist += abs( one->favorite_meal[0] - two->favorite_meal[0] );
	dist += abs( one->favorite_book[0] - two->favorite_book[0] );
	dist += abs( one->last_book[0] - two->last_book[0] );
	dist += abs( one->favorite_dessert[0] - two->favorite_dessert[0] );

	return dist;
}
