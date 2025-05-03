// Test program for C to MIPS compiler
#include <stdio.h>

// Test global variables of different sizes
char global_byte = 42;
short global_short = 1234;
int global_int = 56789;
long long global_long = 123456789LL;

int main() {
    // Test local variables and arithmetic
    int x = 10;
    int y = 20;
    int sum = x + y;
    
    // Test if-then-else
    if (sum > 25) {
        printf("Sum is greater than 25\n");
    } else {
        printf("Sum is not greater than 25\n");
    }
    
    // Test for loop
    int total = 0;
    for (int i = 0; i < 5; i++) {
        total += i;
    }
    
    // Test different sized operations
    char byte_val = global_byte + 1;
    short short_val = global_short + 2;
    int int_val = global_int + 3;
    long long long_val = global_long + 4;
    
    printf("Results:\n");
    printf("byte: %d\n", byte_val);
    printf("short: %d\n", short_val);
    printf("int: %d\n", int_val);
    printf("long: %lld\n", long_val);
    printf("total from loop: %d\n", total);
    
    return 0;
}