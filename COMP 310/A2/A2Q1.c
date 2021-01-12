#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <semaphore.h>
#include <pthread.h>
#include <limits.h>
#include <sys/time.h>

static sem_t rw_mutex;
static sem_t mutex;
static int read_count = 0;

static int sharedVariable = 0;

static int writerRepeatCount = -1;
static int readerRepeatCount = -1;

static double minimumWrite = INT_MAX;
static double maximumWrite = INT_MIN;
static double averageWrite = 0;
static int numberOfWrites = 0;

static double minimumRead = INT_MAX;
static double maximumRead = INT_MIN;
static double averageRead = 0;
static int numberOfReads = 0;

static void *writerFunc(void *arg) {
  for(int i = 0; i < writerRepeatCount; i++){
    struct timeval startTime, stopTime;

    gettimeofday(&startTime, NULL);

    if (sem_wait(&rw_mutex) == -1){
      exit(2);
    }

    gettimeofday(&stopTime, NULL);

    double waitingTime = (double) (stopTime.tv_sec - startTime.tv_sec) * 1000 + (double) (stopTime.tv_usec - startTime.tv_usec) / 1000;

    if(waitingTime < minimumWrite){
      minimumWrite = waitingTime;
    }

    if(waitingTime > maximumWrite){
      maximumWrite = waitingTime;
    }

    numberOfWrites++;
    averageWrite = averageWrite+((waitingTime-averageWrite)/numberOfWrites);

    sharedVariable += 10;

    if (sem_post(&rw_mutex) == -1){
      exit(2);
    }

    useconds_t sleepTime = (useconds_t)((rand()%100)*1000);
    usleep(sleepTime);
  }

  return NULL;
}

static void *readerFunc(void *arg) {
  for(int i = 0; i < readerRepeatCount; i++){
    struct timeval startTime, stopTime;

    gettimeofday(&startTime, NULL);

    if (sem_wait(&mutex) == -1){
      exit(2);
    }

    read_count++;

    if(read_count == 1){
      if (sem_wait(&rw_mutex) == -1){
        exit(2);
      }
    }

    if (sem_post(&mutex) == -1){
      exit(2);
    }

    gettimeofday(&stopTime, NULL);

    double waitingTime = (double) (stopTime.tv_sec - startTime.tv_sec) * 1000 + (double) (stopTime.tv_usec - startTime.tv_usec) / 1000;

    if(waitingTime < minimumRead){
      minimumRead = waitingTime;
    }

    if(waitingTime > maximumRead){
      maximumRead = waitingTime;
    }

    numberOfReads++;
    averageRead = averageRead+((waitingTime-averageRead)/numberOfReads);

    int loc = sharedVariable;
    printf("value: %d\n", loc);

    useconds_t sleepTime = (useconds_t)((rand()%100)*1000);
    usleep(sleepTime);

    if (sem_wait(&mutex) == -1){
      exit(2);
    }

    read_count--;

    if(read_count == 0){
      if (sem_post(&rw_mutex) == -1){
        exit(2);
      }
    }

    if (sem_post(&mutex) == -1){
      exit(2);
    }
  }

  return NULL;
}

int main(int argc, char *argv[]) {
  if(argc != 3){
    printf("Exactly two input parameters are required by the program.");
    return 1;
  }

  writerRepeatCount = atoi(argv[1]);
  readerRepeatCount = atoi(argv[2]);

  if (sem_init(&rw_mutex, 0, 1) == -1) {
    printf("Error init semaphore\n");
    exit(1);
  }

  if (sem_init(&mutex, 0, 1) == -1) {
    printf("Error init semaphore\n");
    exit(1);
  }

  pthread_t writers[10];

  for(int i = 0; i < 10; i++){
    int s = pthread_create(&writers[i], NULL, writerFunc, NULL);

    if (s != 0) {
      printf("Error creating threads\n");
      exit(1);
    }
  }

  pthread_t readers[500];

  for(int i = 0; i < 500; i++){
    int s = pthread_create(&readers[i], NULL, readerFunc, NULL);

    if (s != 0) {
      printf("Error creating threads\n");
      exit(1);
    }
  }

  for(int i = 0; i < 10; i++){
    int s = pthread_join(writers[i], NULL);

    if (s != 0) {
      printf("Error joining threads\n");
      exit(1);
    }
  }

  for(int i = 0; i < 500; i++){
    int s = pthread_join(readers[i], NULL);

    if (s != 0) {
      printf("Error joining threads\n");
      exit(1);
    }
  }

  printf("final value: %d\n", sharedVariable);

  printf("min write: %'.3fms\n", minimumWrite);
  printf("max write: %'.3fms\n", maximumWrite);
  printf("average write: %'.3fms\n", averageWrite);

  printf("min read: %'.3fms\n", minimumRead);
  printf("max read: %'.3fms\n", maximumRead);
  printf("average read: %'.3fms\n", averageRead);
}
