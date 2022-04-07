import java.util.Arrays;

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
		
		// make image size divisible by block size
		numBlock = ((imgWidth + 1) / 2) * ((imgHeight + 1) / 2); 
		
		codeBook = new int[numCluster][numDimension];
		
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
		
		// Initialize codeBook with random number
		for (int y = 0; y < numCluster; y++) {
			int randInt = (int)(Math.random() * 255); // TODO: put an int range? This will just be 0/1
			
			for (int x = 0; x < numDimension; x++) {
				codeBook[y][x] = randInt;
			}
		}
		
		// Quantize
		for (int i = 0; i < numBlock; i++) {
			
			double bestDist = Double.MAX_VALUE;
			int bestIndex = -1;
			
			for (int k = 0; k < numCluster; k++) {
				
				double sum = 0;
				
				for (int j = 0; j < numDimension; j++) {
					int diff = inputVectors[i][j] - codeBook[k][j];
					sum += diff * diff;
				}
				
				double dist = Math.sqrt(sum);
				
				if (bestDist < dist) {
					bestDist = dist;
					bestIndex = k;
				}
			}
			
			quantIndices[i] = bestIndex;
		}
		
		// Update codebook
		double[] sum = new double[numDimension];
		for (int k = 0; k < numCluster; k++) {
			int countVec = 0; // number of input vectors assigned to kth cluster
			
			// clear sum to 0
			Arrays.fill(sum, 0);

			for (int i = 0; i < numBlock; i++) {
				if (quantIndices[i] == k) {
					for (int j = 0; j < numDimension; j++) {
						sum[j] += inputVectors[i][j];
					}	
				}
			}
			
			for (int j = 0; j < numDimension; j++) {
				// the average
				codeBook[k][j] = (int)Math.round(sum[j]/countVec);
			}	
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
