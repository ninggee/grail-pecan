#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#define ARRAY_SIZE  10
#define NUM_THREADS  4

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
int global_array[ARRAY_SIZE];

void init(int index) {
  global_array[index] = 0;
}

void add(int index, int inc) {
  global_array[index] += inc;
}

int foo(int a) {
  int i;
  for(i = 0; i < ARRAY_SIZE; ++i) {
    pthread_mutex_lock(&mutex);
    add(i, a);
    pthread_mutex_unlock(&mutex);
  }
  return a;
}

int bar(int a) {
  int i;
  for(i = 0; i < ARRAY_SIZE; ++i) {
    add(i, a);
  }
  return a;
}

void test_worker(void *ptr) {
  pthread_t tid = pthread_self();
  int seed = (int) tid;
  printf("Hello, thread 0x%016x\n", tid);
  foo(seed);
  bar(seed);
}

int main(int argc, char **argv) {
  int i;
  pthread_t thread[NUM_THREADS];
  printf("Hello, world!\n");
  pthread_mutex_lock(&mutex);
  for(i = 0; i < ARRAY_SIZE; ++i) {
    init(i);
  }
  pthread_mutex_unlock(&mutex);
  for(i = 0; i < NUM_THREADS; ++i) {
    pthread_create(&thread[i], NULL, (void*)test_worker, (void*) 0);
  }
  for(i = 0; i < NUM_THREADS; ++i) {
    pthread_join(thread[i], NULL);
  }
  return 0;
}
