#include <stdio.h>
#include <stdlib.h>
#include "A2_bmp_helpers.h"

int main( int argc, char* argv[] ){

  if( argc != 4 && argc != 6 ){
    printf( "bmp_collage must be run with parameters that allow two images to be collaged.\nUsage: $ %s <bmp_input1> <bmp_input2> <bmp_result> [x_offset] [y_offset], where [param] indicates optional\n", argv[0] );
    exit(EXIT_FAILURE);
  }
  
  int x_offset, y_offset;
  
  if( argc == 6 ){
    x_offset = atoi(argv[4]);
    y_offset = atoi(argv[5]);
  } 
  else {
    x_offset = 0;
    y_offset = 0;
  }
  
  int return_code = bmp_collage( argv[1], argv[2], argv[3], x_offset, y_offset );
  
  if( return_code )
    printf( "bmp_collage function returned an error.\n" );
  else
    printf( "bmp_collage operation completed correctly!\n" );
    
  return return_code;
}
