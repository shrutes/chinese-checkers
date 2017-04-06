import java.util.Comparator;

		class  ArrayComparator implements Comparator<int[]> {
			public int compare(int[] array1, int[] array2) {
				if (array1[0] > array2[0]) {
					return 1;
				}
				else if (array1[0] < array2[0]) {
					return -1;
				}
				else {
					if (array1[1] > array2[1]) {
						return 1;
					}
					else if (array1[1] < array2[1]) {
						return -1;
					}
					else {
						return 0;
					}
				}
			}
		}