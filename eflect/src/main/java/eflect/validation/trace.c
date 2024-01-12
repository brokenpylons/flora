#define _POSIX_C_SOURCE 199309L

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <time.h>

uint64_t timestamp() {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC_RAW, &ts);
    return 1e9 * ts.tv_sec + ts.tv_nsec;
}

bool read(char *file, uint64_t *reading) {
  FILE *f = fopen(file, "r");
  if (f == NULL) {
    return false;
  }
  if (fscanf(f, "%lu", reading) != 1) {
    return false;
  }
  fclose(f);
  return true;
}

int main(int argc, char *argv[]) {
    if (argc != 2) {
      return 1;
    }
    char* file = argv[1];
    uint64_t previous = 0;
    uint64_t reading;

    uint64_t start = timestamp();

    while (true) {
        if (!read(file, &reading)) {
          return 1;
        }

        if (previous == reading) {
            continue;
        }

        uint64_t time = timestamp();
        printf("%lu %lu\n", time - start, reading - previous);
        previous = reading;
     }
}
