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
	int fullWidth, fullHeight; // full image resolution (multiple of 2)
	private int blkWidth, blkHeight; // block resolution
	private int numBlock; // number of blocks in image
	private int numDimension; // number of vector dimension in VQ
	private int numCluster;// number of clusters in VQ
	private int maxIteration; // maximum number of iteration in VQ training
	private int[][] codeBook; // codebook in VQ
	private int[][] R, G, B; // RGB planes of the padded image
	private int[][] inputVectors; // vectors from input image
	private int[][] quantVectors; // vectors for quantized image
	private int[] quantIndices; // quantized indices for blocks

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
		
		// save indices as PPM file
		MImage indexImage = new MImage(fullWidth / 2, fullHeight / 2);
		indices2Image(quantIndices, indexImage, fullWidth / 2, fullHeight / 2);
		String indexName = token[0] + "-index.ppm";
		indexImage.write2PPM(indexName);
		
		// dequantize indices back to vectors
		dequantize(quantIndices, numBlock, quantVectors);
		// write quantized image to file
		MImage quantImage = new MImage(imgWidth, imgHeight);
		vectors2Image(quantVectors, quantImage, width, height);
		String quantName = token[0] + "-quant.ppm";
		quantImage.write2PPM(quantName);
		return 0;
	}
	
	// set up work space
	protected int allocate(int width, int height) {
		imgWidth = width;
		imgHeight = height;
		
		// padded to be divisible by 2 based on block size
		if (width % 2 != 0) {
			fullWidth = width + 1;
		} else {
			fullWidth = width;
		}
		if (height % 2 != 0) {
			fullHeight = height + 1;
		} else {
			fullHeight = height;
		}
		
		numBlock = (fullWidth / 2) * (fullHeight / 2); 
		
		codeBook = new int[numCluster][numDimension];
		
		R = new int[fullHeight][fullWidth];
		G = new int[fullHeight][fullWidth];
		B = new int[fullHeight][fullWidth];
		
		inputVectors = new int[numBlock][numDimension];
		quantVectors = new int[numBlock][numDimension];
		
		quantIndices = new int[numBlock];
		
		return 0;
	}

	// convert one image to vectors in VQ
	protected void image2Vectors(MImage image, int vectors[][], int width, int height) {
		
		// rgb value of a single pixel
		int[] rgb = new int[3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				image.getPixel(x, y, rgb);
				R[y][x] =  rgb[0];
				G[y][x] =  rgb[1];
				B[y][x] =  rgb[2];
			}
		}
		/* if the original image dimensions are not divisible by 2,
		 * the rest is padded with zeros, which is Java's default value.
		 */
		
		int countBlock = 0;
		for (int y = 0; y < fullHeight; y += blkHeight) {
			for (int x = 0; x < fullWidth; x += blkWidth) {
				// rgb values of a block of 4 neighbor pixels
				
				vectors[countBlock][0] = R[y][x];
				vectors[countBlock][1] = G[y][x];
				vectors[countBlock][2] = B[y][x];
				
				vectors[countBlock][3] = R[y][x + 1];
				vectors[countBlock][4] = G[y][x + 1];
				vectors[countBlock][5] = B[y][x + 1];
				
				vectors[countBlock][6] = R[y + 1][x];
				vectors[countBlock][7] = G[y + 1][x];
				vectors[countBlock][8] = B[y + 1][x];
				
				vectors[countBlock][9] = R[y + 1][x + 1];
				vectors[countBlock][10] = G[y + 1][x + 1];
				vectors[countBlock][11] = B[y + 1][x + 1];
				
				countBlock++;
			}
		}
	}

	// convert vectors to one image in VQ
	protected void vectors2Image(int vectors[][], MImage image, int width, int height) {
		
		int countBlock = 0;
		for (int y = 0; y < fullHeight; y += blkHeight) {
			for (int x = 0; x < fullWidth; x += blkWidth) {
				// rgb values of a block of 4 neighbor pixels
				
				R[y][x] = vectors[countBlock][0];
				G[y][x] = vectors[countBlock][1];
				B[y][x] = vectors[countBlock][2];
				
				R[y][x + 1] = vectors[countBlock][3];
				G[y][x + 1] = vectors[countBlock][4];
				B[y][x + 1] = vectors[countBlock][5];
				
				R[y + 1][x] = vectors[countBlock][6];
				G[y + 1][x] = vectors[countBlock][7];
				B[y + 1][x] = vectors[countBlock][8];
				
				R[y + 1][x + 1] = vectors[countBlock][9];
				G[y + 1][x + 1] = vectors[countBlock][10];
				B[y + 1][x + 1] = vectors[countBlock][11];
				
				countBlock++;
			}
		}
		
		// rgb value of a single pixel
		int[] rgb = new int[3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				rgb[0] = R[y][x];
				rgb[1] = G[y][x];
				rgb[2] = B[y][x];
				image.setPixel(x, y, rgb);
			}
		}
		
	}

	// convert indices to one image in VQ
	protected void indices2Image(int indices[], MImage image, int width, int height) {
		int countBlock = 0;
		// rgb value of a single pixel
		int[] rgb = new int[3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				rgb[0] = indices[countBlock];
				rgb[1] = indices[countBlock];
				rgb[2] = indices[countBlock];
				image.setPixel(x, y, rgb);
				
				countBlock++;
			}
		}
	}

	// train codebook with K-means clustering algorithm
	protected void train(int vectors[][], int count) {
		
		// initialize codeBook with random numbers
		for (int y = 0; y < numCluster; y++) {
			int randInt = (int)(Math.random() * 256);
			
			for (int x = 0; x < numDimension; x++) {
				codeBook[y][x] = randInt;
			}
		}
		
		int[] prevIndices = new int[numBlock];
		int[] currentIndices = new int[numBlock];
		for (int i = 0; i < maxIteration; i++) {
			/*
			 * TODO: break conditions
			 * 
			 * Iterate steps 2 & 3 until the algorithm meets a stopping condition 
			 * (i.e. no data points change clusters, 
			 * the sum of the distance is minimized, 
			 * or the maximum number of iterations is reached.)
			 */
			// quantize input image vectors to indices
			quantize(inputVectors, numBlock, currentIndices);
			
			if (Arrays.equals(currentIndices, prevIndices)) {
				// no data points changed clusters
				System.out.println("TESTING: NO DATA POINTS CHANGED CLUSTERS. BREAKING ON i = " + i); // REMOVETHIS
				break;
			}
			
			prevIndices = currentIndices;
		}
		
	}

	// display codebook
	protected void display() {
		System.out.println("CODEBOOK:");
		System.out.println("Index\tArray");
		System.out.println("-------------------------------------------------");
		for (int i = 0; i < numCluster; i++) {
			System.out.println(i + "\t" + Arrays.toString(codeBook[i]));
		}
	}

	// quantize vectors to indices
	protected void quantize(int vectors[][], int count, int indices[]) {
		// quantize
		for (int i = 0; i < numBlock; i++) {
			
			double bestDist = Double.MAX_VALUE;
			int bestIndex = -1;
			
			for (int k = 0; k < numCluster; k++) {
				
				double sum = 0;
				
				for (int j = 0; j < numDimension; j++) {
					int diff = vectors[i][j] - codeBook[k][j];
					sum += diff * diff;
				}
				
				double dist = Math.sqrt(sum);
				
				if (dist < bestDist) {
					bestDist = dist;
					bestIndex = k;
				}
			}
			
			indices[i] = bestIndex;
		}
		
		// update codebook
		double[] sum = new double[numDimension];
		for (int k = 0; k < numCluster; k++) {
			count = 0; // number of input vectors assigned to kth cluster
			
			// clear sum to 0
			Arrays.fill(sum, 0);
			
			for (int i = 0; i < numBlock; i++) {
				if (indices[i] == k) {
					count++;
					for (int j = 0; j < numDimension; j++) {
						sum[j] += vectors[i][j];
					}	
				}
			}
			
			for (int j = 0; j < numDimension; j++) {
				// the average
				codeBook[k][j] = (int)Math.round(sum[j]/count);
			}	
		}
	}

	// dequantize indices to vectors
	protected void dequantize(int indices[], int count, int vectors[][]) {
		for (int i = 0; i < numBlock; i++) {
			vectors[i] = codeBook[indices[i]];
		}
	}
}
