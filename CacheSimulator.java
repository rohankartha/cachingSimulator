package org.example;

import java.io.IOException;
import java.util.*;

/**
 * Simulates four different caching methods (main method at the bottom of this file).
 * Two "reader" objects are provided: one for long-trace and one for short-trace. The reader for
 * long-trace is currently commented-out
 *
 */
public class CacheSimulator {
    public static String tagBits;
    public static String setBits;
    public static String offsetBits;
    public static Map<Character, String> hexToBin = new HashMap<>();
    public static Map<String, Character> binToHex = new HashMap<>();
    public static List<String> modes = new ArrayList<>();
    public static Map<String, Integer> linesPerSet = new HashMap<>();

    // True: more verbose, False: less verbose
    public static boolean verbosity = false;


    public CacheSimulator() {}

    public static void setUp() {
        hexToBin.put('0', "0000");
        hexToBin.put('1', "0001");
        hexToBin.put('2', "0010");
        hexToBin.put('3', "0011");
        hexToBin.put('4', "0100");
        hexToBin.put('5', "0101");
        hexToBin.put('6', "0110");
        hexToBin.put('7', "0111");
        hexToBin.put('8', "1000");
        hexToBin.put('9', "1001");
        hexToBin.put('a', "1010");
        hexToBin.put('b', "1011");
        hexToBin.put('c', "1100");
        hexToBin.put('d', "1101");
        hexToBin.put('e', "1110");
        hexToBin.put('f', "1111");

        binToHex.put("0000", '0');
        binToHex.put("0001", '1');
        binToHex.put("0010", '2');
        binToHex.put("0011", '3');
        binToHex.put("0100", '4');
        binToHex.put("0101", '5');
        binToHex.put("0110", '6');
        binToHex.put("0111", '7');
        binToHex.put("1000", '8');
        binToHex.put("1001", '9');
        binToHex.put("1010", 'a');
        binToHex.put("1011", 'b');
        binToHex.put("1100", 'c');
        binToHex.put("1101", 'd');
        binToHex.put("1110", 'e');
        binToHex.put("1111", 'f');

        modes.add("direct-mapped");
        modes.add("2-way-set-associative");
        modes.add("4-way-set-associative");
        modes.add("fully-associative");

        linesPerSet.put("direct-mapped", 1);
        linesPerSet.put("2-way-set-associative", 2);
        linesPerSet.put("4-way-set-associative", 4);
        linesPerSet.put("fully-associative", 64);
    }


    /**
     * Convert 4-bit binary to hex digit
     *
     * @param binaryCode
     * @return
     */
    public static String binaryToHex(String binaryCode) {
        StringBuilder builder = new StringBuilder();
        builder.append(binaryCode);
        int remainder = 4 - (binaryCode.length() % 4);

        // if 4 digits < 4
        if (remainder != 4) {
            builder.reverse();
            for (int i = 0; i < remainder; i++) {
                builder.append("0");
            }
            builder.reverse();
        }

        int begIndex = 0;
        int endIndex = 4;
        StringBuilder fourDigitRetriever = new StringBuilder();
        fourDigitRetriever.append(builder);
        StringBuilder hexString = new StringBuilder();

        while (endIndex <= builder.length() + 1) {
            String fourDigitCode = fourDigitRetriever.substring(begIndex, endIndex);
            hexString.append(binToHex.get(fourDigitCode));

            begIndex += 4;
            endIndex += 4;
        }
        return hexString.toString();
    }


    /**
     * @param lines
     * @return
     */
    public static List<String> hexToBinary(List<String> lines) {
        List<String> binaryForm = new ArrayList<>();
        String line = null;

        for (String s : lines) {
            line = s;
            StringBuilder stringBuilder = new StringBuilder();

            for (int j = 0; j < line.length(); j++) {
                String binaryCode = hexToBin.get(line.charAt(j));

                if (binaryCode != null) {
                    stringBuilder.append(binaryCode);
                }
            }
            binaryForm.add(String.valueOf(stringBuilder));
        }
        return binaryForm;
    }


    /**
     * @param address
     * @param cachingApproach
     */
    public static void parseAddress(String address, String cachingApproach) {
        offsetBits = address.substring(28, 32);

        switch (cachingApproach) {
            case "direct-mapped":
                setBits = address.substring(22, 28);
                tagBits = address.substring(0, 22);
                break;
            case "2-way-set-associative":
                setBits = address.substring(23, 28);
                tagBits = address.substring(0, 23);
                break;

            case "4-way-set-associative":
                setBits = address.substring(24, 28);
                tagBits = address.substring(0, 24);
                break;

            case "fully-associative":
                setBits = address.substring(28, 28);
                tagBits = address.substring(0, 28);
        }
    }


    /**
     * @param reader
     * @param cachingCode
     * @param binaryForm
     */
    public static void simulateCache(Reader reader, int cachingCode, List<String> binaryForm) {
        int hits = 0;
        int misses = 0;


        Map<String, CacheSet> sets = new HashMap<>();
        String cachingMethod = modes.get(cachingCode);
        int linesInSet = linesPerSet.get(cachingMethod);

        System.out.println("-------------------------------------------------");
        System.out.println("mode: " + cachingMethod);
        System.out.println("64 blocks, 16 bytes in blocks; " + (64/linesPerSet.get(cachingMethod)) + " sets, " + linesPerSet.get(cachingMethod) + " lines per set");
        System.out.println("-------------------------------------------------");


        for (int i = 0; i < reader.lines.size(); i++) {
            parseAddress(binaryForm.get(i), cachingMethod);

            // Print header of output
            if (verbosity) {
                System.out.print(reader.lines.get(i).charAt(0));
                System.out.print(i + 1 + "  addr " + "0x");
                System.out.print(reader.lines.get(i).substring(1, 10) + "  ");
                System.out.print("Looking for tag " + "0x" + binaryToHex(tagBits));
                System.out.println(" in set " + "0x" + binaryToHex(setBits));
                System.out.println("State of set " + "0x" + binaryToHex(setBits) + ":");
            }

            // Lines are empty
            if (!sets.containsKey(binaryToHex(setBits))) {
                CacheSet cacheSet = new CacheSet();
                cacheSet.setSetIndex(binaryToHex(setBits));
                cacheSet.initializeLines(linesInSet);

                if (verbosity) {
                    for (int j = 0; j < linesInSet; j++) {
                        System.out.print("line " + cacheSet.setOfLines.get(j).getLineNumber() + " ");
                        System.out.print("V=" + cacheSet.setOfLines.get(j).getValid() + " ");
                        System.out.print("tag 0x" + cacheSet.setOfLines.get(j).getTag() + " ");
                        System.out.println("last_touch=" + cacheSet.setOfLines.get(j).getLast_touch());
                    }
                }
                cacheSet.setOfLines.get(0).setValid(1);
                cacheSet.setOfLines.get(0).setTag(binaryToHex(tagBits));
                cacheSet.setOfLines.get(0).setLast_touch(i + 1);
                sets.put(binaryToHex(setBits), cacheSet);

                if (verbosity) {
                    System.out.println("Miss! Found empty line 0x0; adding block there; setting last_touch to " + (i + 1));
                }
                misses++;
            }

            // Line is not empty
            else {
                CacheSet cacheSet = sets.get(binaryToHex(setBits));
                boolean check = false;
                int index = 0;

                for (int a = 0; a < linesInSet; a++) {
                    if (Objects.equals(cacheSet.setOfLines.get(a).getTag(), binaryToHex(tagBits))) {
                        check = true;
                        index = a;
                    }
                }

                if (check) {
                    if (cacheSet.setOfLines.get(index).getValid() == 1) {
                        if (verbosity) {
                            for (int b = 0; b < linesInSet; b++) {

                                System.out.print("line " + cacheSet.setOfLines.get(b).getLineNumber() + " ");
                                System.out.print("V=" + cacheSet.setOfLines.get(b).getValid() + " ");
                                System.out.print("tag 0x" + cacheSet.setOfLines.get(b).getTag() + " ");
                                System.out.println("last_touch=" + cacheSet.setOfLines.get(b).getLast_touch());
                            }
                        }
                        if (verbosity) {
                            System.out.println("Found it in line 0x" + cacheSet.setOfLines.get(index).getLineNumber() + "Hit! Updating last_touch to " + (i + 1));
                        }
                        hits++;
                        cacheSet.setOfLines.get(index).setLast_touch(i+1);
                    }
                } else {
                    if (verbosity) {
                        for (int b = 0; b < linesInSet; b++) {
                            System.out.print("line " + cacheSet.setOfLines.get(b).getLineNumber() + " ");
                            System.out.print("V=" + cacheSet.setOfLines.get(b).getValid() + " ");
                            System.out.print("tag 0x" + cacheSet.setOfLines.get(b).getTag() + " ");
                            System.out.println("last_touch=" + cacheSet.setOfLines.get(b).getLast_touch());
                        }
                    }


                    int c = 0;
                    int leastRecentlyUsed = 0;
                    while (cacheSet.setOfLines.get(c).getValid() != 0 && c < linesInSet - 1) {
                        c++;
                        if (cacheSet.setOfLines.get(leastRecentlyUsed).getLast_touch() > cacheSet.setOfLines.get(c).getLast_touch()){
                            leastRecentlyUsed = c;
                        }
                    }
                    if (cacheSet.setOfLines.get(c).getValid() == 0) {
                        if (verbosity) {
                            System.out.println("Miss! Found empty line 0x" + c + " adding block there; setting last_touch to " + (i + 1));
                        }
                        misses++;
                        cacheSet.setOfLines.get(c).setTag(binaryToHex(tagBits));
                        cacheSet.setOfLines.get(c).setLast_touch(i + 1);
                        cacheSet.setOfLines.get(c).setValid(1);
                    } else {
                        if (verbosity) {
                            System.out.println("Miss! Evicting line 0x" + leastRecentlyUsed + "adding block there; setting last_touch to " + (i + 1));
                        }
                        misses++;
                        cacheSet.setOfLines.get(leastRecentlyUsed).setTag(binaryToHex(tagBits));
                        cacheSet.setOfLines.get(leastRecentlyUsed).setLast_touch(i + 1);
                    }
                }
            }
            if (verbosity) {
                System.out.println("-------------------------------------------------");
            }
        }
        System.out.println("-------------------------------------------------");
        System.out.println("Hits: " + hits + "; Misses: " + misses + "; Addresses: " + reader.lines.size());
        System.out.println((double)hits/(double)reader.lines.size() + " hr, " + (double)misses/(double)reader.lines.size() + " mr");
    }


    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        setUp();

        // long trace
        //Reader reader = new Reader("/Users/rohankartha/Library/CloudStorage/OneDrive-DartmouthCollege/Fall2023/CS 51/Caching/src/main/java/org/example/long-trace.txt");

        // short trace
        Reader reader = new Reader("/Users/rohankartha/Library/CloudStorage/OneDrive-DartmouthCollege/Fall2023/CS 51/Caching/src/main/java/org/example/sample.txt");

        reader.read();
        List<String> binaryForm = hexToBinary(reader.lines);

        // Set cachingCode as 0 for direct-mapped, 1 for 2-way-set-associative, 2 for 4-way-set-associative, 3 for set-associative
        simulateCache(reader, 0, binaryForm);
    }
}
