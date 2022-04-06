
/*******************************************************
 * CS4551 Multimedia Software Systems
 * 
 * Spring 2022 Homework #2 Framework
 * 
 * ColorImageVectorQuantizer.java
 * 
 * By Yi Zhao 03/14/2022
 *******************************************************/

public class ColorImageVectorQuantizer {
	private int imgWidth, imgHeight; // image resolution
	private int blkWidth, blkHeight; // block resolution
	private int numBlock; // number of blocks in image
	private int numDimension; // number of vector dimension in VQ
	private int numCluster;// number of clusters in VQ
	private int maxIteration; // maximum number of iteration in VQ training
	private int[][] codeBook; // codebook in VQ
	private int inputVectors[][]; // vectors from input image
	private int quantVectors[][]; // vectors for quantized image
	private int quantIndices[]; // quantized indices for blocks

	// He said the most important part of VQ is that each row is a vector. We process row by row.
	
	public ColorImageVectorQuantizer() {
		blkWidth = 2;
		blkHeight = 2;
		numDimension = blkWidth * blkHeight * 3;
		numCluster = 256;
		maxIteration = 100;
	}

	public int process(String inputName) {
		// read 24-bit color image from PPM file
		MImage inputImage = new MImage(inputName);
		System.out.println(inputImage);
		String token[] = inputName.split("\\.");
		// set up workspace
		int width = inputImage.getW();
		int height = inputImage.getH();
		allocate(width, height);
		// form vector from input image
		image2Vectors(inputImage, inputVectors, imgWidth, imgHeight);
		// train vector quantizer
		train(inputVectors, numBlock);
		// display trained codebook
		display();
		// quantize input image vectors to indices
		quantize(inputVectors, numBlock, quantIndices);
		// TOFIX - add code to save indices as PPM file
		// dequantize indices back to vectors
		dequantize(quantIndices, numBlock, quantVectors);
		// write quantized image to file
		MImage quantImage = new MImage(imgWidth, imgHeight);
		vectors2Image(quantVectors, quantImage, width, height);
		String quantName = token[0] + "-quant.ppm";
		quantImage.write2PPM(quantName);
		return 0;
	}
	
	// TOFIX - add code to set up work space
	protected int allocate(int width, int height) {
		imgWidth = width;
		imgHeight = height;
		
		// TODO: careful about non-divisible by block size, it should still occupy a block.
		numBlock = (imgWidth * imgHeight) / (blkWidth * blkHeight); 
		
		codeBook = new int[numCluster][numDimension];
		// TODO: initialize codeBook with random number .. not sure where this goes.
		
		inputVectors = new int[numBlock][numDimension];
		quantVectors = new int[numBlock][numDimension];
		
		quantIndices = new int[numBlock];
		
		return 0;
	}

	// TOFIX - add code to convert one image to vectors in VQ
	protected void image2Vectors(MImage image, int vectors[][], int width, int height) {
	}

	// TOFIX - add code to convert vectors to one image in VQ
	protected void vectors2Image(int vectors[][], MImage image, int width, int height) {
	}

	// TOFIX - add code to convert indices to one image in VQ
	protected void indices2Image(int indices[], MImage image, int width, int height) {
	}

	// TOFIX - add code to train codebook with K-means clustering algorithm
	protected void train(int vectors[][], int count) {
	
		// quantize step 1 - not sure where this goes, but it looks like K-means algo
		for (int i = 0; i < numBlock; i++) {
			// work on ith block
			int bestRow;
			int bestDist = Integer.MAX_VALUE;
			for (int k = 0; k < numCluster; k++) {
				// TODO: calculate dist between Si and Ck. May not need squareroot, since we only care about which is bigger.
				// Not sure what S is - sample vector i.e inputVectors?, C is the codeBook.
				
				int sum = 0;
				
				for (int j = 0; j < 12; j++) {
					int diff = inputVectors[i][j] - codeBook[k][j];
					sum += diff * diff;
				}
				
				if (bestDist > sum) {
					bestDist = sum;
					bestRow = k;
				}
			}
			
			// TODO: store bestRow to index[i]
		}
		
		
		// step 2 - update codeBook ... not sure where this goes
		for (int k = 0; k < 256; k++) {
			int countVec = 0; // how many vectors assigned to this cluster
			int[] sum = new int[12];
			
			// find average
			for (int i = 0; i < numBlock; i++) {
				/*
				if (index[i] == k) {
					
					for (int j ...) {
						sum[j] += S[i][j];
					}
				}
				*/
			}
			
			/*
			 * TODO:
			 
			codeBook[k] = sum[?] / countVec;
			This is a vector.
			*/
		}
	}

	// TOFIX - add code to display codebook
	protected void display() {
	}

	// TOFIX - add code to quantize vectors to indices
	protected void quantize(int vectors[][], int count, int indices[]) {
	}

	// TOFIX - add code to dequantize indices to vectors
	protected void dequantize(int indices[], int count, int vectors[][]) {
	}
}
