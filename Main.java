import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
	private static final String START_OF_FUNCTION_LINE = ">>";
	private static final Pattern DIGITS_PATTERN = Pattern.compile("(^\\d)*(\\d+)");
	private static List<String> _input;

	public static void main(String[] args) {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in))) {
			_input = buffer.lines().collect(Collectors.toList());
			List<String[]> referenceList = createReferenceList();
			int[] occurences = createOccurences(referenceList);
			Integer[] indexInSortedOrderParent = createIndexInSortedOrder(occurences);
			generateAndPrintOutput(indexInSortedOrderParent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void generateAndPrintOutput(Integer[] indexInSortedOrderParent) {
		String[] permutedInput = permute(indexInSortedOrderParent);

		for (int i = 0; i < permutedInput.length; i++) {
			String line = permutedInput[i];
			if (line.startsWith(START_OF_FUNCTION_LINE)) {
				int lastIndex = 0;
				StringBuilder output = new StringBuilder();
				Matcher matcher = DIGITS_PATTERN.matcher(line);
				while (matcher.find()) {
					int index = getIndex(matcher.group(2), indexInSortedOrderParent);
					output.append(line, lastIndex, matcher.start()).append(index);
					lastIndex = matcher.end();
				}
				int length = line.length();
				if (lastIndex < length) {
					output.append(line, lastIndex, length);
				}
				line = output.toString();
			}
			System.out.println(line);
		}
	}

	private static Integer[] createIndexInSortedOrder(int[] occurence) {
		int length = occurence.length;
		List<Item> tempList = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			boolean containsInput = _input.get(i).contains("Input");
			tempList.add(new Item(occurence[i], i, containsInput));
		}
		Collections.sort(tempList, Collections.reverseOrder());

		Integer[] indexInSortedOrder = new Integer[length];
		for (int i = 0; i < length; ++i) {
			indexInSortedOrder[i] = tempList.get(i).getIndex();
		}
		return indexInSortedOrder;
	}

	private static int[] createOccurences(List<String[]> references) {
		int[] occurence = new int[references.size()];
		for (String[] reference : references) {
			for (String number : reference) {
				if (number.length() > 0) {
					occurence[(Integer.valueOf(number) - 1) % occurence.length]++;
				}
			}
		}
		return occurence;
	}

	private static List<String[]> createReferenceList() {
		return _input.stream().map(x -> {
			String xy = "";
			if (x.startsWith(START_OF_FUNCTION_LINE)) {
				xy = x.replaceAll("[^\\d]", " ");
			} else {
				xy = " ";
			}
			return xy.trim().replaceAll("[^\\d]", " ").split(" ");
		}).collect(Collectors.toList());
	}

	private static int getIndex(String token, Integer[] indexInSortedOrderParent) {
		Integer number = Integer.valueOf(token);
		List<Integer> ints = Arrays.asList(indexInSortedOrderParent);
		return ints.indexOf((number - 1) % ints.size()) + 1;
	}

	private static String[] permute(Integer[] indexInSortedOrderParent) {
		int length = indexInSortedOrderParent.length;
		String[] permutedArray = new String[length];
		for (int i = 0; i < length; i++) {
			permutedArray[i] = _input.get(indexInSortedOrderParent[i]);
		}
		return permutedArray;
	}
}

class Item implements Comparable<Item> {
	private final int _value;
	private final int _index;
	private final boolean _isInputLine;

	Item(int value, int index, boolean isInput) {
		_value = value;
		_index = index;
		_isInputLine = isInput;
	}

	@Override
	public int compareTo(Item other) {
		if (_isInputLine && other._isInputLine) {
			if (getIndex() > other.getIndex()) {
				return -1;
			} else if (getIndex() < other.getIndex()) {
				return 1;
			}
			return 0;
		}
		if (_value < other._value) {
			return -1;
		} else if (_value > other._value) {
			return 1;
		}
		return 0;
	}

	public int getIndex() {
		return _index;
	}
}
