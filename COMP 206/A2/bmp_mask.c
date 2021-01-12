/* FILE: bmp_mask.c is provided as part of COMP 206 A2. Do not modify this file.
 *       Its purpose is to test that you have implemented bmp_mask correctly for
 *       Question 2. When you're finished, the output of this program should match
 *       the examples in the assignment document.
 */

#include <stdio.h>
#include <stdlib.h>
#include "A2_bmp_helpers.h"

int main( int argc, char* argv[] ){

  if( argc < 10 ){
    printf( "bmp_mask must be run with the parameters of a square to be colorized.\nUsage: $ %s <bmp_input_file> <bmp_output_file> x_min y_min x_max y_max R G B\n", argv[0] );
    exit(EXIT_FAILURE);
  }
  
  int x_min = atoi(argv[3]);
  int y_min = atoi(argv[4]);
  int x_max = atoi(argv[5]);
  int y_max = atoi(argv[6]);

  int red   = atoi(argv[7]);
  int green = atoi(argv[8]);
  int blue  = atoi(argv[9]);
  
  if( red < 0 || red > 255 ){ fprintf( stderr, "Error: argument red must be an integer between 0 and 255. Got %d.\n", red ); return -1; }
  if( green  < 0 || green > 255 ){ fprintf( stderr, "Error: argument green must be an integer between 0 and 255. Got %d.\n", green ); return -1; }
  if( blue < 0 || blue > 255 ){ fprintf( stderr, "Error: argument blue must be an integer between 0 and 255. Got %d.\n", blue ); return -1; }
  
  if( x_max < x_min ){ fprintf( stderr, "Error: x_max must be greater or equal to x_min.\n" ); return -1; }
  if( y_max < y_min ){ fprintf( stderr, "Error: y_max must be greater or equal to y_min.\n" ); return -1; }
  
  if( x_min < 0 ){ fprintf( stderr, "Error: x_min must be zero or greater.\n" ); return -1; }
  if( y_min < 0 ){ fprintf( stderr, "Error: y_min must be zero or greater.\n" ); return -1; }
  
  // NOTE: This is the line that executes the bmp_mask function. It shows how your code will be called.
  int return_code = bmp_mask( argv[1], argv[2], x_min, y_min, x_max, y_max, red, green, blue );
  
  if( return_code )
    printf( "bmp_mask function returned an error.\n" );
  else
    printf( "bmp_mask operation completed correctly!\n" );
    
  return return_code;
  
}

