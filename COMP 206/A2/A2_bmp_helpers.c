/* FILE: A2_bmp_helpers.c is where you will code your answers for Assignment 2.
 * 
 * Each of the functions below can be considered a start for you. 
 *
 * You should leave all of the code as is, except for what's surrounded
 * in comments like "REPLACE EVERTHING FROM HERE... TO HERE.
 *
 * The assignment document and the header A2_bmp_headers.h should help
 * to find out how to complete and test the functions. Good luck!
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <assert.h>

int bmp_open( char* bmp_filename,        unsigned int *width, 
              unsigned int *height,      unsigned int *bits_per_pixel, 
              unsigned int *padding,     unsigned int *data_size, 
              unsigned int *data_offset, unsigned char** img_data ){

              
  // YOUR CODE FOR Q1 SHOULD REPLACE EVERYTHING FROM HERE
  //step 1:
  FILE *bmpfile = fopen(bmp_filename, "rb");
  char b, m;
  fread(&b, 1, 1, bmpfile);
  fread(&m, 1, 1, bmpfile);
  if(b == 'B' && m == 'M'){
    //data_size
  	fread(data_size, 1, 4, bmpfile);
  	fclose(bmpfile);
  	//step 2:
  	unsigned char *heap = (unsigned char*)malloc(*data_size);
  	bmpfile = fopen(bmp_filename, "rb");
  	fread(heap, 1, *data_size, bmpfile);
  	//img_data
  	*img_data = heap;
  	fclose(bmpfile);
    //step 3:
    char data[*data_size];
    bmpfile = fopen(bmp_filename, "rb");
    fread(data, 1, *data_size, bmpfile);
    fclose(bmpfile);
    //data offset
    unsigned int* data_offset_temp;
    data_offset_temp = (unsigned int*) (data+10);
    *data_offset = *data_offset_temp;
    //width
    unsigned int* width_temp;
    width_temp = (unsigned int*) (data+18);
    *width = *width_temp;
    //height
    unsigned int* height_temp;
  	height_temp = (unsigned int*) (data+22);
    *height = *height_temp;
  	//bits per pixel
    unsigned int* bits_per_pixel_temp;
  	bits_per_pixel_temp = (unsigned int*) (data+28);
    *bits_per_pixel = *bits_per_pixel_temp;
    //step 4:
    //padding
    if((((*width)*(*bits_per_pixel))/8)%4 != 0){
  		double d = ((*width)*(*bits_per_pixel))/32;
  		int i = (int) (d+1);
  		int t = i*4;
        *padding = t-((*width)*(*bits_per_pixel))/8;
    }else{
	  	*padding = 0;
    }
  }else{
  	return 0;
  }
  // TO HERE
  
  return 0;  
}

// We've implemented bmp_close for you. No need to modify this function
void bmp_close( unsigned char **img_data ){

  if( *img_data != NULL ){
    free( *img_data );
    *img_data = NULL;
  }
}

int bmp_mask( char* input_bmp_filename, char* output_bmp_filename, 
              unsigned int x_min, unsigned int y_min, unsigned int x_max, unsigned int y_max,
              unsigned char red, unsigned char green, unsigned char blue )
{
  unsigned int img_width;
  unsigned int img_height;
  unsigned int bits_per_pixel;
  unsigned int data_size;
  unsigned int padding;
  unsigned int data_offset;
  unsigned char* img_data    = NULL;
  
  int open_return_code = bmp_open( input_bmp_filename, &img_width, &img_height, &bits_per_pixel, &padding, &data_size, &data_offset, &img_data ); 
  
  if( open_return_code ){ printf( "bmp_open failed. Returning from bmp_mask without attempting changes.\n" ); return -1; }
 
  // YOUR CODE FOR Q2 SHOULD REPLACE EVERYTHING FROM HERE
    unsigned char copy[data_size];
    memcpy(copy, img_data, data_size);
    int count = data_offset;
    int bytes_per_pixel = bits_per_pixel/8;
    for(int y = 0; y<img_height; y++){
        for(int x = 0; x<img_width; x++){
            if(y >= y_min && y <= y_max && x >= x_min && x <= x_max){
                //24 bits per pixel
                copy[count] = red;
                copy[count+1] = green;
                copy[count+2] = blue;
            }
            count = count+bytes_per_pixel;
        }
        for (int z = 0; z<padding; z++){
            count++;
        }
    }
    FILE *bmpfile = fopen(output_bmp_filename, "w");
    fwrite(copy, 1, data_size, bmpfile);
    fclose(bmpfile);
  // TO HERE!
  
  bmp_close( &img_data );
  
  return 0;
}         

int bmp_collage( char* bmp_input1, char* bmp_input2, char* bmp_result, int x_offset, int y_offset ){

  unsigned int img_width1;
  unsigned int img_height1;
  unsigned int bits_per_pixel1;
  unsigned int data_size1;
  unsigned int padding1;
  unsigned int data_offset1;
  unsigned char* img_data1    = NULL;
  
  int open_return_code = bmp_open( bmp_input1, &img_width1, &img_height1, &bits_per_pixel1, &padding1, &data_size1, &data_offset1, &img_data1 ); 
  
  if( open_return_code ){ printf( "bmp_open failed for %s. Returning from bmp_collage without attempting changes.\n", bmp_input1 ); return -1; }
  
  unsigned int img_width2;
  unsigned int img_height2;
  unsigned int bits_per_pixel2;
  unsigned int data_size2;
  unsigned int padding2;
  unsigned int data_offset2;
  unsigned char* img_data2    = NULL;
  
  open_return_code = bmp_open( bmp_input2, &img_width2, &img_height2, &bits_per_pixel2, &padding2, &data_size2, &data_offset2, &img_data2 ); 
  
  if( open_return_code ){ printf( "bmp_open failed for %s. Returning from bmp_collage without attempting changes.\n", bmp_input2 ); return -1; }
  
  // YOUR CODE FOR Q3 SHOULD REPLACE EVERYTHING FROM HERE
    
    //exit if bpp of input images differ
    if(bits_per_pixel1 != bits_per_pixel2){
        return -1;
    }
    
    //calculate width and height
    unsigned int new_width = img_width1;
    unsigned int new_height = img_height1;
    if(y_offset+img_height2>img_height1 && x_offset+img_width2>img_width1){
        new_width = x_offset+img_width2;
        new_height = y_offset+img_height2;
    }else if(y_offset+img_height2<=img_height1 && x_offset+img_width2>img_width1){
        new_width = x_offset+img_width2;
    }else if(y_offset+img_height2>img_height1 && x_offset+img_width2<=img_width1){
        new_height = y_offset+img_height2;
    }
    if(x_offset<0 && y_offset<0){
        new_width = img_width1 + abs(x_offset);
        new_height = img_height1 + abs(y_offset);
    }else if(x_offset<0 && y_offset>=0){
        new_width = img_width1 + abs(x_offset);
    }else if(x_offset>=0 && y_offset<0){
        new_height = img_height1 + abs(y_offset);
    }
    
    //calculate padding
    unsigned int new_padding = padding1;
    if((((new_width)*(bits_per_pixel1))/8)%4 != 0){
        double d = ((new_width)*(bits_per_pixel1))/32;
        int i = (int) (d+1);
        int t = i*4;
        new_padding = t-((new_width)*(bits_per_pixel1))/8;
    }else{
        new_padding = 0;
    }
    
    //edit header data
    int ovr_size = data_offset1+(new_height*(new_width*(bits_per_pixel1/8)+new_padding));
    unsigned char new_img[ovr_size];
    memcpy(new_img, img_data1, data_offset1);
    char ovr_size_arr[4];
    memcpy(ovr_size_arr, &ovr_size, 4);
    char width_arr[4];
    memcpy(width_arr, &new_width, 4);
    char height_arr[4];
    memcpy(height_arr, &new_height, 4);
    for(int z = 0; z<4; z++){
        new_img[2+z] = ovr_size_arr[z];
        new_img[18+z] = width_arr[z];
        new_img[22+z] = height_arr[z];
    }
    
    //shift
    int x_shift = 0;
    int y_shift = 0;
    if(x_offset<0 && y_offset<0){
        x_shift = abs(x_offset);
        y_shift = abs(y_offset);
        x_offset = 0;
        y_offset = 0;
    }else if(x_offset<0 && y_offset>=0){
        x_shift = abs(x_offset);
        x_offset = 0;
    }else if(x_offset>=0 && y_offset<0){
        y_shift = abs(y_offset);
        y_offset = 0;
    }
    
    //print
    int ovr_count = data_offset1;
    int img1_count = data_offset1;
    int img2_count = data_offset2;
    int bytes_per_pixel = bits_per_pixel1/8;
    for(int y = 0; y<new_height; y++){
        for(int x = 0; x<new_width; x++){
            if(y >= y_offset && y <= y_offset+img_height2-1 && x >= x_offset && x <= x_offset+img_width2-1){
                new_img[ovr_count] = img_data2[img2_count];
                new_img[ovr_count+1] = img_data2[img2_count+1];
                new_img[ovr_count+2] = img_data2[img2_count+2];
                
                img2_count += bytes_per_pixel;
                if(x == x_offset+img_width2-1){
                    for (int z = 0; z<padding2; z++){
                        img2_count++;
                    }
                }
                if(y >= 0+y_shift && y <= img_height1-1+y_shift && x >= 0+x_shift && x <= img_width1-1+x_shift){
                    img1_count = img1_count+bytes_per_pixel;
                    if(x == img_width1-1+x_shift){
                        for (int z = 0; z<padding1; z++){
                            img1_count++;
                        }
                    }
                }
            }else if(y >= 0+y_shift && y <= img_height1-1+y_shift && x >= 0+x_shift && x <= img_width1-1+x_shift){
                new_img[ovr_count] = img_data1[img1_count];
                new_img[ovr_count+1] = img_data1[img1_count+1];
                new_img[ovr_count+2] = img_data1[img1_count+2];
                
                img1_count = img1_count+bytes_per_pixel;
                if(x == img_width1-1+x_shift){
                    for (int z = 0; z<padding1; z++){
                        img1_count++;
                    }
                }
            }else{
                unsigned char white = (unsigned char) 255;
                new_img[ovr_count] = white;
                new_img[ovr_count+1] = white;
                new_img[ovr_count+2] = white;
            }
            ovr_count = ovr_count+bytes_per_pixel;
        }
        for (int z = 0; z<new_padding; z++){
            ovr_count++;
        }
    }
    
    //write file
    FILE *bmpfile = fopen(bmp_result, "w");
    fwrite(new_img, 1, ovr_size, bmpfile);
    fclose(bmpfile);
  // TO HERE!
      
  bmp_close( &img_data1 );
  bmp_close( &img_data2 );
  
  return 0;
}                  
