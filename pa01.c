/*=============================================================================
| Assignment: pa01 - Encrypting a plaintext file using the Hill cipher
|
| Author: Edward Rosales
| Language: c, c++, Java, go, python
|
| To Compile: javac pa01.java
| gcc -o pa01 pa01.c
| g++ -o pa01 pa01.cpp
| go build pa01.go
|
| To Execute: java -> java pa01 kX.txt pX.txt
| or c++ -> ./pa01 kX.txt pX.txt
| or c -> ./pa01 kX.txt pX.txt
| or go -> ./pa01 kX.txt pX.txt
| or python -> python3 pa01.py kX.txt pX.txt
| where kX.txt is the keytext file
| and pX.txt is plaintext file
|
| Note: All input files are simple 8 bit ASCII input
|
| Class: CIS3360 - Security in Computing - Summer 2023
| Instructor: McAlpin
| Due Date: 6/23/2023
|
+=============================================================================*/

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>

int modulo(int a, int b) {
    int result = a % b;
    if (result < 0)
        result += b;
    return result;
}

void multiplyMatrix(int **key, int plain[][1], int result[][1], int size) {
    int i, j, k;
    for (i = 0; i < size; i++) {
        for (j = 0; j < 1; j++) {
            result[i][j] = 0;
            for (k = 0; k < size; k++) {
                result[i][j] += key[i][k] * plain[k][j];
            }
            result[i][j] = modulo(result[i][j], 26);
        }
    }
}

void processPlainText(char *plaintext) {
    int len = strlen(plaintext);
    int i, j;

    // Convert to lowercase
    for (i = 0; i < len; i++) {
        plaintext[i] = tolower(plaintext[i]);
    }

    // Strip non-alphabetic characters
    j = 0;
    for (i = 0; i < len; i++) {
        if (plaintext[i] >= 'a' && plaintext[i] <= 'z') {
            plaintext[j] = plaintext[i];
            j++;
        }
    }
    plaintext[j] = '\0';
}

void printMatrix(int **matrix, int size) {
    printf("Key Matrix:\n");
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            printf("%d ", matrix[i][j]);
        }
        printf("\n");
    }
    printf("\n");
}

void printPaddedPlaintext(char *plaintext, int padding) {
    printf("Plaintext:");
    int len = strlen(plaintext);
    for (int i = 0; i < len + padding; i++) {
        if(i%80 == 0)
            printf("\n");
        if(i >= len)
            plaintext[i] = 'x';

        printf("%c", plaintext[i]);

    }

    printf("\n\n");
}

int main(int argc, char *argv[]) {
    if (argc != 3) {
        printf("Usage: %s <key_matrix_file> <plaintext_file>\n", argv[0]);
        return 1;
    }

    FILE *plainFile, *keyFile;
    keyFile = fopen(argv[1], "r");
    plainFile = fopen(argv[2], "r");

    if (plainFile == NULL || keyFile == NULL) {
        printf("Error opening file.\n");
        return 1;
    }

    // Read the key matrix size
    int size;
    fscanf(keyFile, "%d\n", &size);


    // Matrix size is not valid
    if (size < 2 || size > 9) {
        printf("Invalid key matrix size.\n");
        return 1;
    }

    // Dynamically allocate memory for the key matrix
    int **key = (int **)malloc(size * sizeof(int *));
    int i, j;
    for (i = 0; i < size; i++) {
        key[i] = (int *)malloc(size * sizeof(int));
        for (j = 0; j < size; j++) {
            fscanf(keyFile, "%d", &key[i][j]);
        }
    }

    printMatrix(key, size);

    // Read and process the plaintext
    char plaintext[10000];
    char ch;
    int count = 0;
    while ((ch = fgetc(plainFile)) != EOF) {
        if (isalpha((unsigned char)ch)) {
            plaintext[count] = tolower((unsigned char)ch);
            count++;
        }
    }
    plaintext[count] = '\0';

    processPlainText(plaintext);
    int len = strlen(plaintext);

    // Calculate the number of padding characters needed
    int padding = size - (len % size);
    if (padding == size)
        padding = 0;

    printPaddedPlaintext(plaintext, padding);

    // Encrypt the plaintext using the Hill cipher
    int plain[9][1], result[9][1];
    int charCount = 0;  // Track the number of characters printed

    printf("Ciphertext:\n");
    for (i = 0; i < len; i += size) {
        for (j = 0; j < size; j++) {
            plain[j][0] = plaintext[i + j] - 'a';
        }

        multiplyMatrix(key, plain, result, size);

        for (j = 0; j < size; j++) {
            printf("%c", (char)(result[j][0] + 'a'));

            // Check if 80 characters have been printed
            charCount++;


            if (charCount % 80 == 0)
                printf("\n");
        }
    }

    printf("\n");

    fclose(plainFile);
    fclose(keyFile);

    // Free dynamically allocated memory
    for (i = 0; i < size; i++) {
        free(key[i]);
    }
    free(key);

    return 0;
}
/*=============================================================================
| I Edward Rosales ed234644 affirm that this program is
| entirely my own work and that I have neither developed my code together with
| any another person, nor copied any code from any other person, nor permitted
| my code to be copied or otherwise used by any other person, nor have I
| copied, modified, or otherwise used programs created by others. I acknowledge
| that any violation of the above terms will be treated as academic dishonesty.
+=============================================================================*/