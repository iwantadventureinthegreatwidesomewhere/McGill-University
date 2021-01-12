#!/bin/bash

make
data="data"
files=`ls data`

for i in $files; do
	mv $data/$i query
	./movie_recommender query/$i data/*
	mv query/$i data
done 
	