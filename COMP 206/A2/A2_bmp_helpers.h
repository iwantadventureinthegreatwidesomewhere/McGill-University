/* FILE: A2_bmp_helpers.h
 * 
 * BRIEF: Contains specifications for the functions you must implement
 *        for Assignment 2.
 *
 * DATE: Feb 20, 2018
 */

/* FUNCTION: bmp_open
 * 
 * BRIEF:    Attempts to read the header information of bmp_filename to
 *           populate the file information variables. If successful,
 *           creates sufficient heap memory to store the entire image 
 *           including both the header and the pixel data. Then reads
 *           the image from file into this memory.
 *
 * RETURN:   0 if image is read successfully. Then all params must be valid.
 *           -1 for any error. Then user code should not use any params.
 * 
 * PARAMS:   bmp_filename   [IN]  The path to the BMP file to be opened
 *           width          [OUT] The width of each image row
 *           height         [OUT] The height of each image column
 *           bits_per_pixel [OUT] BBP = num_colors * 8 for all BMPs we will use
 *           row_padding    [OUT] Additional bytes placed at the end of each row
 *                                to ensure the row size is a multiple of 4
 *           data_size      [OUT] The number of bytes in img_data, includes header
 *           header_size    [OUT] The number of bytes just for the header. Usually 56.
 *           img_data       [OUT] Pointer to the image data on the heap.
 * 
 */ 
int bmp_open( char* bmp_filename,        unsigned int *width, 
              unsigned int *height,      unsigned int *bits_per_pixel, 
              unsigned int *padding,     unsigned int *data_size, 
              unsigned int *data_offset, unsigned char** img_data );

/* FUNCTION: bmp_close
 *
 * BRIEF:    frees the memory that was allocated on the heap by any successful bmp_open
 * 
 * PARAMS:   img_data [IN/OUT] Pointer to the image data on heap. Must be free'd and 
 *                             set to NULL. It is up to the calling code to ensure the
 *                             address at *img_data is valid heap memory.
 */
void bmp_close( unsigned char **img_data );

/* FUNCTION: bmp_mask 
 * 
 * BRIEF:    Opens bmp_filename and sets the pixels within the mask region to the
 *           specified color. All image coordinates are expressed relative to (0,0) 
 *           at the bottom left of the image. X indicates horizontal position, 
 *           increasing left to right. Y indicates the vertical position, increasing
 *           bottom to top.
 * 
 * RETURN:   0 if successful
 *           -1 on any error, such as failure to open image
 * 
 * PARAMS:   in_bmp_filename  [IN]  The source BMP file to read             
 *           out_bmp_filename [IN]  The new BMP to be created. Copy of in_bmp_filename
 *                                  except with the mask region set to the new color.
 *           x_min            [IN]  The lowest x value (column) to be included in the mask 
 *           y_min            [IN]  The lowest y value (row) to be included in the mask
 *           x_max            [IN]  The highest x value (column) to include in the the mask
 *           y_max            [IN]  The highest y value (row) to include in the mask
 *           red              [IN]  The red value to set for each mask pixel
 *           green            [IN]  The green value to set for each mask pixel
 *           blue             [IN]  The blue value to set for each mask pixel
 *
 */ 
int bmp_mask( char* input_bmp_filename, char* output_bmp_filename, 
              unsigned int x_min,       unsigned int y_min,               
              unsigned int x_max,       unsigned int y_max,
              unsigned char red,        unsigned char green, unsigned char blue );
              
/* FUNCTION: bmp_collage
 * 
 * BRIEF:    Opens the two input images and "collages" bmp_input2 on top of bmp_input1
 *           with result written in bmp_result. Collaging operation means pasting 
 *           input1 into a canvas and shifting input2 by x_off y_off and then pasting 
 *           it "on top". So, anywhere in the output that you could possibly find a pixel
 *           from both input images, the pixel from bmp_input2 MUST be the one shown.
 * 
 * RETURN:   0 if successful
 *           -1 on any error, such as failure to open image
 * 
 * PARAMS:   bmp_input1       [IN]  The first image, collaged on the bottom.
 *           bmp_input2       [IN]  The second image, collaged on top.
 *           bmp_result       [IN]  The output, big enough to contain the result.
 *           x_offset         [IN]  Number of pixels to shift input2 horizontally (positive=right)
 *           y_offset         [IN]  Number of pixels to shift input2 vertically (positive=up)
 *
 */ 
int bmp_collage( char* bmp_input1, char* bmp_input2, 
                 char* bmp_result, int x_offset, 
                 int y_offset );          
