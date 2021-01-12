#!/bin/bash

./bmp_collage metro_top_left.bmp metro_top_right.bmp metro_collage.bmp 135 65
./bmp_collage metro_collage.bmp metro_mid_left.bmp metro_collage2.bmp 0 -167
./bmp_collage metro_collage2.bmp metro_mid_right.bmp metro_collage3.bmp 108 83 
./bmp_collage metro_collage3.bmp metro_bot_left.bmp metro_collage4.bmp 0 -213 
./bmp_collage metro_collage4.bmp metro_bot_right.bmp metro_collage_full.bmp 132 0

