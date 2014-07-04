package uk.ac.ncl.cs.esc.newpartitiontool;




import java.util.ArrayList;

import java.util.List;


import uk.ac.ncl.cs.esc.read.WorkflowTemplate;

/**
 * @author ZequnLi
 *         Date: 14-4-25
 */
public class Normal {
//    private  int[][] servicesSecurity; //connection matrix ,-1 refers to no relation
//    private  int[][] servicesCommunication;  //data transfer
//    private  int[][] servicesCPUCost;        //cpu cost on different platform
//    private  int[][] platformInfo;           //platform security, input and output cost

    double[][] workflow;
    int[][] dataSecurity;
    double [][] ccost;
    double [][] cpucost;
    int [] cloud;
    int [][] ssecurity;
 
    public Normal(WorkflowTemplate w){
        this.workflow = w.getWorkflow();
        this.dataSecurity = w.getDataSecurity();
        this.ccost = w.getCcost();
        this.cpucost = w.getCpucost();
        this.cloud = w.getCloud();
        this.ssecurity = w.getSsecurity();
    }
      // check

    public List<List<Integer>> sort(){
        // platform sort  ordered by security level
        int minLvl= Integer.MAX_VALUE;
        int maxLvl = Integer.MIN_VALUE;
        final List<List<Integer>> sortedPlatform = new ArrayList<List<Integer>>();
        //find max security lvl
        for(int i =0;i<cloud.length;i++){
            final int current = cloud[i];
            if(current<minLvl){
                minLvl = current;
            }
            if(current>maxLvl){
                maxLvl = current;
            }
        }

        //init
        for(int i =0;i<maxLvl+1;i++){
            sortedPlatform.add(null);
        }
        // order and cluster clouds by its security
        for(int i =0;i<cloud.length;i++){
            final int current = cloud[i];
            List<Integer> list = sortedPlatform.get(current);
            if(null == list){
                List<Integer> temp = new ArrayList<Integer>();
                temp.add(i);
                sortedPlatform.set(current,temp);
            } else {
                list.add(i);
            }
        }
        //
        List<List<Integer>> combination = new ArrayList<List<Integer>>();
        //for each service get all possible deployment
        final List<List<Integer>> possibleDeploy = new ArrayList<List<Integer>>();
        for(int i=0;i<ssecurity.length;i++){
            List<Integer> list = new ArrayList<Integer>();
            int min = ssecurity[i][1]; //location
            //int min = minLvl;
            int max = maxLvl;
            // consider data security
            int dataMin = calMinDataSecurity(i);
            if(min<dataMin) min = dataMin;

            while (min<=max){
                if(sortedPlatform.get(min) != null){
                    list.addAll(sortedPlatform.get(min));
                }
                min++;
            }
            possibleDeploy.add(list);
        }
      
        //get Permutation
        //get maximum choices
        int max = 0;
        for(List list: possibleDeploy){
            if(list.size()>max){
                max = list.size();
            }
        }
        // regard it as a max * service size full matrix
        double it = Math.pow(max,possibleDeploy.size()) -1;
       // System.out.println("number of it"+it);
        while (it>=0){
            boolean ignore = false;
            List<Integer> list = new ArrayList<Integer>();
            double temp = it;
            for(int m =0;m<possibleDeploy.size();m++){
                if(temp % max >= possibleDeploy.get(m).size()){
                    ignore = true;
                    break;
                }
                list.add(possibleDeploy.get(m).get((int)(temp%max)));
                temp/=max;
            }
            if(!ignore){
                //result
                combination.add(list);
            }
            it--;
        }
        return combination;
        // all combination get

    }

    public List<Integer> sortBest(){
        // platform sort  ordered by security level
        int minLvl= Integer.MAX_VALUE;
        int maxLvl = Integer.MIN_VALUE;
        final List<List<Integer>> sortedPlatform = new ArrayList<List<Integer>>();
        //find max security lvl
        for(int i =0;i<cloud.length;i++){
            final int current = cloud[i];
            if(current<minLvl){
                minLvl = current;
            }
            if(current>maxLvl){
                maxLvl = current;
            }
        }

        //init
        for(int i =0;i<maxLvl+1;i++){
            sortedPlatform.add(null);
        }
        // order and cluster clouds by its security
        for(int i =0;i<cloud.length;i++){
            final int current = cloud[i];
            List<Integer> list = sortedPlatform.get(current);
            if(null == list){
                List<Integer> temp = new ArrayList<Integer>();
                temp.add(i);
                sortedPlatform.set(current,temp);
            } else {
                list.add(i);
            }
        }
     //   System.out.println("sp:"+sortedPlatform);
        //

        //for each service get all possible deployment
        final List<List<Integer>> possibleDeploy = new ArrayList<List<Integer>>();
        for(int i=0;i<ssecurity.length;i++){
            List<Integer> list = new ArrayList<Integer>();
            int min = ssecurity[i][1]; //location
            //int min = minLvl;
            int max = maxLvl;
            // consider data security
            int dataMin = calMinDataSecurity(i);
            if(min<dataMin) min = dataMin;

            while (min<=max){
                if(sortedPlatform.get(min) != null){
                    list.addAll(sortedPlatform.get(min));
                }
                min++;
            }
           
            possibleDeploy.add(list);
        }
      //  System.out.println("sp:"+possibleDeploy);
        //get Permutation
        //get maximum choices
        int max = 0;
        for(List list: possibleDeploy){
            if(list.size()>max){
                max = list.size();
            }
        }
        // regard it as a max * service size full matrix
        double it = Math.pow(max,possibleDeploy.size()) -1;

        double min = Double.MAX_VALUE;
        List<Integer> best = null;

        // System.out.println("number of it"+it);
        while (it>=0){
            boolean ignore = false;
            List<Integer> list = new ArrayList<Integer>();
            double temp = it;
            for(int m =0;m<possibleDeploy.size();m++){
                if(temp % max >= possibleDeploy.get(m).size()){
                    ignore = true;
                    break;
                }
                list.add(possibleDeploy.get(m).get((int)(temp%max)));
                temp/=max;
            }
    //      System.out.println(list);
            if(!ignore){
                //result
                double re =  this.calCost(list);
                if(min>re){
                    min =re;
                    best = list;
                }
            }
            it--;
        }
        return best;
    }

    private int calMinDataSecurity(int pos){
        int result = -1;
        for(int i = 0;i<this.workflow.length;i++){
            if(this.workflow[pos][i] != -1){
                if(result< this.dataSecurity[pos][i]){
                    result = this.dataSecurity[pos][i];
                }
            }
            if(this.workflow[i][pos] != -1){
                if(result<this.dataSecurity[i][pos]){
                    result = this.dataSecurity[i][pos];
                }
            }
        }
        return result;
    }

    public double calCost(List<Integer> combination){
        double result = 0;
        for(int i =0;i<combination.size();i++){
            int c = combination.get(i);
            result+= this.cpucost[i][c];
            // output and input cost
            for(int j = 0;j<this.workflow.length;j++){
                // calculate only two services deploy in different cloud.
                if(c!= combination.get(j)){
                    double outputData = this.workflow[i][j];
                    if(outputData != -1){
                       result+= this.ccost[c][combination.get(j)] * outputData;
                    }
                }
            }

        }
        return result;
    }

   
    public static void print(int [][] result){
        for(int h=0;h<result.length;h++){
            for(int f=0;f<result[h].length;f++){
                System.out.print(result[h][f]+",");
            }
            System.out.println("");
        }
    }
    public static void print(int [] result){
        for(int h = 0;h<result.length;h++){
            System.out.print(result[h]+",");
        }
    }
}