package uk.ac.ncl.cs.esc.read;

import java.util.*;

/**
 * @author ZequnLi
 *         Date: 14-4-30
 */
public class RandomInt {
    public static int randomInt(int min,int max){
        if(min == max) return max;
        Random rand = new Random();

        return rand.nextInt((max - min) + 1) + min;
    }
    public static long randomLong(long min,long max){
        if(min == max) return max;
       // Random rand = new Random();

        return RandomInt.nextLong((max - min) + 1) + min;

    }
    private static long nextLong( long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        Random rng = new Random();
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits-val+(n-1) < 0L);
        return val;
    }
    public static List<Integer> randomInt(int min,int max, int size){

        List<Integer> list  = new ArrayList<Integer>();
        if(max - min+1 <size){
            throw new IllegalArgumentException();
        }
        Set<Integer> set = new HashSet< Integer >();
        while (set.size()<size){
            int temp = randomInt(min,max);
            if(set.contains(temp)) continue;
            set.add(temp);
            list.add(temp);
        }
        return list;
    }
    public static boolean randomBoolean(double chance){
        final int precision =1000;
        int range = (int)(chance*precision);
        int pos = randomInt(1,precision);
        if(pos<=range){
            return true;
        }
        return false;

    }
}