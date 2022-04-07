CS4551 Multimedia Project 2
Jessie George

Task 1: VQ


Task 2: DCT
Class variables:
I set up conversion matrices, and DCT coefficient range as per the question.
I included a blockSize variable to be used in all loops
(but a warning that if the user wants to 
change the blockSize, they may need to review other settings too, like adjusting 
the image size to be divisible by any blockSize etc. I didn't do that since it's 
out of scope of the current project).
I initialized the default quant tables as per the question.

I commented out calling setWorkQuantTable in the decode function, since it 
is already set globally when called in the encode function.

allocate function sets up the resolutions and size of arrays.

setWorkQuantTable function adjusts the compression quality of 
quant tables globally.

extract/combine planes is self-explanatory.

For the conversion between RGB and YCbCr functions, I did the 
matrix multiplications step by step. I kept things in the specified range.

For the conversion between chrominance 444 and 420 functions, 
I averaged 4 neighboring pixels in the subsample.
I assigned that average to 4 neighboring pixels in the upsample. 

For the encode/decode plane functions,
I followed the DCT/IDCT formulae respectively,
and the quantization/dequantization formulae.
I coded it inplace, because I worked on this before we discussed the
temp array solution in class.

--------
CS4551 Multimedia Software Systems
@ Author: Elaine Kang
Computer Science Department
California State University, Los Angeles

Compile requirement
======================================
JDK Version 7.0 or above


Compile Instruction on Command Line:
======================================
javac CS4551_Main.java MImage.java 
or 
javac *.java


Execution Instruction on Command Line:
======================================
java CS4551_Main [inputfile]
e.g.
java CS4551_Main Ducky.ppm
