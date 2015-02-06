/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahb_fx1.mcdc;

/**
 *
 * @author ariful
 */
import java.util.*;

public class PSTG {

    static int t_value = 0;
    static boolean display_help = false;
    static int no_of_parameter = 0;
    static String binary_setting;
    static int data[];    
//    @todo change here
//    static ArrayList<String> binary_cmd_list = new ArrayList<String>();
//    static ArrayList<String> interaction_element_list = new ArrayList<String>();
//    static ArrayList<String> final_test_suite_list = new ArrayList<String>();
    private ArrayList<String> binary_cmd_list;
    private ArrayList<String> interaction_element_list;
    private ArrayList<String> final_test_suite_list;

    private ArrayList Indexing_Array;
    
//    private int max_weight;
    private int Indexing_value;
    private int Previous_bainary_setting = 0;
    private int Indexing_index;

    private int particlesize;
    private int max_iteration;
    //static int stoping_condition=0;

    public void setParticleSize(int value) {
        this.particlesize = value;

    }

    public int getParticleSize() {
        return this.particlesize;
    }

    public void setMaxIteration(int value) {
        this.max_iteration = value;
    }

    public int getMaxIteration() {
        return this.max_iteration;
    }

    public ArrayList<String> searchPS(String[] args) {

        /**
         * ******************************************************************
         * Read the request from the args wich is taken form args, which is used
         * the notation "-i 3,4, -t 2" which is -i is the starting , 3,4 are
         * values and parameters and -t is the starting of t values , 2 means
         * 2-way
         */
        
        interaction_element_list    = new ArrayList<String>();
        binary_cmd_list             = new ArrayList<String>();
        final_test_suite_list       = new ArrayList<String>();
        Indexing_Array              = new ArrayList();
        
//        String[] args = new String[];
//        args= psvalue.toString();
//        System.out.println("Args: "+ args);
        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-i")) {

                String data_str = new String();
                 // specify the nimber of values which is comiing after -i
                // that is means the second index i+1
                if (i + 1 < args.length) {
                    i++;// go to the index of the parameter values
                    // Print the parameter Values
                    System.out.println ("Parameters & Values => "+args[i]);
                    //assign parameter values to data_str
                    data_str = args[i];
                }

                // assign data values automatically to data by commas
                StringTokenizer s = new StringTokenizer(data_str, ",");
                // count number of parameters which is p.
                int p = data_str.replaceAll("[^,]", "").length();
                p++; // as an array size
                 System.out.println("the value of P is equals ="+p);//me
                // make an array with p dimention to store the values.
                data = new int[p];
                int k = 0;
                while (s.hasMoreTokens()) {
                    //assign eahc parameter values to an index of k in
                    //in the data array
                    data[k] = Integer.parseInt(s.nextToken());
                    // System.out.println("the value of data[0]"+data[k]);//me
                    k++;
                }

            } /**
             * ***************************************************************
             */
            else if (args[i].equals("-t")) {
                // specify t value
                if (i + 1 < args.length) {
                    i++;
                    t_value = Integer.parseInt(args[i]);
                  System.out.println ("Interaction Strength (t) => "+t_value);
                }
            }
        }

//        Display Result
        // if no command line specified or data length is empty
        // or display help requested then display the command line help
        if (args.length == 0 || display_help || data.length == 0) {
            display_command_line_help();
        } else {
            // Indexing_Array.add(0);
            generate_binary_input_combinations(t_value, binary_cmd_list);
            
            System.out.println("cmd list: "+binary_cmd_list + " : iterational element "+interaction_element_list );
            generate_interaction_elements(binary_cmd_list, interaction_element_list);
            this.Indexing_Array.add(interaction_element_list.size());

//            System.out.println("INdexing_Arraylist= "+Indexing_Array);            
//            display_list ("Interaction Element List (before generation)",interaction_element_list);
            ////////////////////////////////////////////////////////////
            generate_final_test_suite(binary_cmd_list, final_test_suite_list);
//            display_list ("Final Suite List",final_test_suite_list);
            // display_list ("Interaction Element List (before generation)",interaction_element_list);

        }

        return final_test_suite_list;

    }//end main

    //*****************************************************************
    //          DISPLAY COMMAND LINE HELP
    //*****************************************************************
    private final static void display_command_line_help() {
        System.out.println("\n\n\t------------------------------");
        System.out.println("\t    COMMAND LINE USAGE");
        System.out.println("\t------------------------------");
        System.out.println("\t-h Display help");
        System.out.println("\t-e Display exhaustive (up to t=10)");
        System.out.println("\t-i <input1, input2,...inputN >");
        System.out.println("\t-c <var1:var2:..varN,...>");
        System.out.println("\t-t <2..7>");
        System.out.println("\t-m <maximum iteration per search>");
        System.out.println("\t-------------------------------\n");
    }
      //*****************************************************************
    //           GENERATE POSSIBLE BINARY INPUT COMBINATIONS
    //           BASED ON THE SELECTION OF T VALUE
    //*****************************************************************

    private final static void generate_binary_input_combinations(int t_value,
            ArrayList<String> binary_cmd_list) {

        int limit = (int) Math.pow(2, data.length);
        for (int i = 0; i < limit; i++) {
            String comb = new String();
            comb = Integer.toBinaryString(i);
            //  System.out.println("the value of comb is ="+comb);//me
            while (comb.length() < data.length) {
                comb = "0" + comb;// add zeros to equal the length to the length
            }                              // of data , if lenth of data is 5 so the program
            // will convert the string 10 to 00010
            //System.out.println("the value of comb after while loop is ="+comb);//me
            // count the occurences of 1 in comb
            int no_of_one = 0;
            for (int j = 0; j < comb.length(); j++) {
                if (comb.charAt(j) == '1') {
                    no_of_one++;
                }
            }
            if (no_of_one == t_value) {
                binary_cmd_list.add(comb);

            }
            //  System.out.println("binary_cmd_list=>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+binary_cmd_list);//me
        }
    }

    private void generate_interaction_elements(ArrayList<String> binary_cmd_list,
            ArrayList<String> interaction_element_list) {
        
        for (Iterator it = binary_cmd_list.iterator(); it.hasNext();) {
            String s = (String) it.next();  // Downcasting is required pre Java 5.
//             System.out.println("the value of S = "+s);
            append_interaction_elements(s, interaction_element_list);
        }
        
    }//end function
      //*****************************************************************
    //           DISPLAY COMBINATIONS BASED ON BINARY SETTING
    //*****************************************************************

    private void append_interaction_elements(String binary_setting,
            ArrayList<String> interaction_element_list) {
        int t = 0;

        int Current_bainary_setting = Integer.parseInt(binary_setting, 2);
        int decimal = 0;

        //determine t
        for (int i = 0; i < binary_setting.length(); i++) {
            if (binary_setting.charAt(i) == '1') {
                t++;
            }
        }

        // copy the combinatorial matrix
        int[] comb = new int[t];
        int index = 0;
        for (int i = 0; i < binary_setting.length(); i++) {
            if (binary_setting.charAt(i) == '1') {
                comb[index++] = data[i];
            }
        }
        // System.out.println(t+"-Way Elements for setting => "+binary_setting);
        if (t == 2) {
            for (int i = 0; i < comb[0]; i++) {
                for (int j = 0; j < comb[1]; j++) {
                    // rearrange data correctly
                    int[] item = new int[data.length];
                    String element = new String("");

                    int layer = 1;
                    for (int z = 0; z < data.length; z++) {
                        if (binary_setting.charAt(z) == '1') {
                            if (layer == 1) {
                                item[z] = i;
                            } else if (layer == 2) {
                                item[z] = j;
                            }
                            layer++;
                        } else {
                            item[z] = -1;
                        }
                    }
                    decimal = Integer.parseInt(binary_setting, 2);
                    for (int z = 0; z < data.length; z++) {
                        if (z > 0) {
                            element = element + ":" + item[z];
                        } else {
                            element = element + item[z];
                        }
                    }
                    this.Indexing_value++;
                    if ((this.Previous_bainary_setting == 0) || (Current_bainary_setting != this.Previous_bainary_setting)) {
                        if (this.Indexing_value - 1 != 0) {
                            this.Indexing_Array.add(this.Indexing_value - 1);
                        }
                    }
                    interaction_element_list.add(element);
                    this.Previous_bainary_setting = Current_bainary_setting;
                }
            }
        } else if (t == 3) {
            for (int i = 0; i < comb[0]; i++) {
                for (int j = 0; j < comb[1]; j++) {
                    for (int k = 0; k < comb[2]; k++) {
                        // rearrange data correctly
                        int[] item = new int[data.length];
                        String element = new String("");
                        int layer = 1;
                        for (int z = 0; z < data.length; z++) {
                            if (binary_setting.charAt(z) == '1') {
                                if (layer == 1) {
                                    item[z] = i;
                                } else if (layer == 2) {
                                    item[z] = j;
                                } else if (layer == 3) {
                                    item[z] = k;
                                }
                                layer++;
                            } else {
                                item[z] = -1;
                            }
                        }
                        decimal = Integer.parseInt(binary_setting, 2);

                        for (int z = 0; z < data.length; z++) {
                            if (z > 0) {
                                element = element + ":" + item[z];
                            } else {
                                element = element + item[z];
                            }
                        }
                        this.Indexing_value++;
                        if ((this.Previous_bainary_setting == 0) || (Current_bainary_setting != this.Previous_bainary_setting)) {
                            if (this.Indexing_value - 1 != 0) {
                                this.Indexing_Array.add(this.Indexing_value - 1);
                            }
                        }
                        interaction_element_list.add(element);
                        this.Previous_bainary_setting = Current_bainary_setting;
                    }
                }
            }
        } else if (t == 4) {
            for (int i = 0; i < comb[0]; i++) {
                for (int j = 0; j < comb[1]; j++) {
                    for (int k = 0; k < comb[2]; k++) {
                        for (int l = 0; l < comb[3]; l++) {
                            // rearrange data correctly
                            int[] item = new int[data.length];
                            String element = new String("");
                            int layer = 1;
                            for (int z = 0; z < data.length; z++) {
                                if (binary_setting.charAt(z) == '1') {
                                    if (layer == 1) {
                                        item[z] = i;
                                    } else if (layer == 2) {
                                        item[z] = j;
                                    } else if (layer == 3) {
                                        item[z] = k;
                                    } else if (layer == 4) {
                                        item[z] = l;
                                    }
                                    layer++;
                                } else {
                                    item[z] = -1;
                                }
                            }
                            decimal = Integer.parseInt(binary_setting, 2);

                            for (int z = 0; z < data.length; z++) {
                                if (z > 0) {
                                    element = element + ":" + item[z];
                                } else {
                                    element = element + item[z];
                                }
                            }
                            this.Indexing_value++;
                            if ((this.Previous_bainary_setting == 0) || (Current_bainary_setting != this.Previous_bainary_setting)) {
                                if (this.Indexing_value - 1 != 0) {
                                    this.Indexing_Array.add(this.Indexing_value - 1);
                                }
                            }
                            interaction_element_list.add(element);
                            this.Previous_bainary_setting = Current_bainary_setting;
                        }
                    }
                }
            }

        } else if (t == 5) {
            for (int i = 0; i < comb[0]; i++) {
                for (int j = 0; j < comb[1]; j++) {
                    for (int k = 0; k < comb[2]; k++) {
                        for (int l = 0; l < comb[3]; l++) {
                            for (int m = 0; m < comb[4]; m++) {
                                // rearrange data correctly
                                int[] item = new int[data.length];
                                String element = new String("");
                                int layer = 1;
                                for (int z = 0; z < data.length; z++) {
                                    if (binary_setting.charAt(z) == '1') {
                                        if (layer == 1) {
                                            item[z] = i;
                                        } else if (layer == 2) {
                                            item[z] = j;
                                        } else if (layer == 3) {
                                            item[z] = k;
                                        } else if (layer == 4) {
                                            item[z] = l;
                                        } else if (layer == 5) {
                                            item[z] = m;
                                        }
                                        layer++;
                                    } else {
                                        item[z] = -1;
                                    }
                                }
                                decimal = Integer.parseInt(binary_setting, 2);

                                for (int z = 0; z < data.length; z++) {
                                    if (z > 0) {
                                        element = element + ":" + item[z];
                                    } else {
                                        element = element + item[z];
                                    }
                                }
                                this.Indexing_value++;
                                if ((this.Previous_bainary_setting == 0) || (Current_bainary_setting != this.Previous_bainary_setting)) {
                                    if (this.Indexing_value - 1 != 0) {
                                       this.Indexing_Array.add(this.Indexing_value - 1);
                                    }
                                }
                                interaction_element_list.add(element);
                                this.Previous_bainary_setting = Current_bainary_setting;
                            }
                        }
                    }
                }
            }
        } else if (t == 6) {
            for (int i = 0; i < comb[0]; i++) {
                for (int j = 0; j < comb[1]; j++) {
                    for (int k = 0; k < comb[2]; k++) {
                        for (int l = 0; l < comb[3]; l++) {
                            for (int m = 0; m < comb[4]; m++) {
                                for (int n = 0; n < comb[5]; n++) {
                                    // rearrange data correctly
                                    int[] item = new int[data.length];
                                    String element = new String("");
                                    int layer = 1;
                                    for (int z = 0; z < data.length; z++) {
                                        if (binary_setting.charAt(z) == '1') {
                                            if (layer == 1) {
                                                item[z] = i;
                                            } else if (layer == 2) {
                                                item[z] = j;
                                            } else if (layer == 3) {
                                                item[z] = k;
                                            } else if (layer == 4) {
                                                item[z] = l;
                                            } else if (layer == 5) {
                                                item[z] = m;
                                            } else if (layer == 6) {
                                                item[z] = n;
                                            }
                                            layer++;
                                        } else {
                                            item[z] = -1;
                                        }
                                    }
                                    decimal = Integer.parseInt(binary_setting, 2);

                                    for (int z = 0; z < data.length; z++) {
                                        if (z > 0) {
                                            element = element + ":" + item[z];
                                        } else {
                                            element = element + item[z];
                                        }
                                    }
                                    this.Indexing_value++;
                                    if ((this.Previous_bainary_setting == 0) || (Current_bainary_setting != this.Previous_bainary_setting)) {
                                        if (this.Indexing_value - 1 != 0) {
                                            this.Indexing_Array.add(this.Indexing_value - 1);
                                        }
                                    }
                                    interaction_element_list.add(element);
                                    this.Previous_bainary_setting = Current_bainary_setting;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
//*****************************************************************
    //           DISPLAY ANY STRING ARRAY LIST
    //*****************************************************************

    private final static void display_list(String title, ArrayList<String> list) {
        int i = 0;
        System.out.println(title);
        for (Iterator it = list.iterator(); it.hasNext();) {
            String s = (String) it.next();  // Downcasting is required pre Java 5.
            System.out.println("i = " + (i + 1) + "->" + s);
            i++;
        }
        if (i == 0) {
            System.out.println("The given list is empty.");
        }
    }

   //*****************************************************************
    //           GENERATE FINAL TEST SUITE
    //*****************************************************************
    private void generate_final_test_suite(ArrayList<String> binary_cmd_list,
            ArrayList<String> final_test_suite_list) {
        
        System.out.println("====================================================");
        
        int max_weight;
        max_weight = this.binary_cmd_list.size();
        
        System.out.println("max_weight="+max_weight);

        
        int particle_size = this.particlesize;
        int stoping_condition = 0;

        Random r = new Random();
        int[][] swarm1 = new int[particle_size][data.length];//declaration of the swarm search space martix.
        double[][] velocity = new double[particle_size][data.length]; // declaration of the velocity martix.
        int[] LBest = new int[data.length];
        
        int Max_iteration = this.max_iteration;
        
        
        StringBuilder sbuf = new StringBuilder();
        String current_test_case = new String("");
        String previous_test_case = new String("");
        String First_LBest_Str = new String("");
        
        System.out.println("the array size is ="+this.interaction_element_list.size());
        System.out.println("Stopping condition : "+stoping_condition );
        

        while (stoping_condition < this.interaction_element_list.size()) {
//            System.out.println("interacgtion element size =>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+interaction_element_list.size());
            System.out.println("the stoping condition => "+stoping_condition + " interacgtion size :"+this.interaction_element_list.size());
            boolean stop = false;
            int iteration = 0;
    //////////////////////////////////////////////////////////////
            ///////////////Initialize the Swarm Randomly//////////////////
            //the initialization will be column by column due to the /////
            //values intered in the Values array, it will start randomly//
            //////////////////between 0 and Values[i]/////////////////////
            //////////////////////////////////////////////////////////////
            for (int i = 0; i < data.length; i++) {//note here that the dimention of the swarm
                for (int j = 0; j < particle_size; j++) {       //and the notation of the dimention is
                    int k = r.nextInt(data[i]); // is deffer due to the notation
                    //int l=r.nextInt(15);        // of the swarm1[j][i] nooot swarm1[i][j]
                    swarm1[j][i] = k;              // note here
                }
            }
            /**
             * **************************************
             * Test
             */
   // for (int i=0;i<particle_size;i++){
            //    for (int j=0;j<data.length;j++){
            //     System.out.print(" "+swarm1[i][j]);
            // }
            //   System.out.println();
            //  }

            /**
             * ********************************************************************
             */
        /////////////////////////////////////////////////////////////
            //////// Initialize the Velocity Martix//////////////////////
            /////////////////////////////////////////////////////////////
            for (int i = 0; i < particle_size; i++) {
                for (int j = 0; j < data.length; j++) {
                    velocity[i][j] = 0.0;
                }
            }
            /**
             * ************************************************************************
             */
// choose the first particle to be the LBest
            for (int j = 0; j < data.length; j++) {
                LBest[j] = swarm1[0][j];
                if (j > 0) {
                    First_LBest_Str = First_LBest_Str + ":" + LBest[j];
                } else {
                    First_LBest_Str = "" + LBest[j];
                }

            }
//            System.out.println("the value of LBest="+First_LBest_Str);

            /**
             * ********************************************************************
             * int previous_weight = check_weight (First_LBest_Str,
             * interaction_element_list);
 /**************************************************************************
             */
            int previous_weight = 0;//check_weight(First_LBest_Str, interaction_element_list);
            while (!stop) {
                for (int particle = 0; particle < particle_size; particle++) {//for each particle

                    for (int j = 0; j < data.length; j++) {
                        if (j > 0) {
                            sbuf = sbuf.append(":").append(swarm1[particle][j]);
                      //  current_test_case = current_test_case +":"+swarm1[particle][j];
                            //   System.out.println("sbuf ="+sbuf);//me
                        } else {
                            sbuf = sbuf.append("").append(swarm1[particle][j]);
                       // current_test_case = "" + swarm1[particle][j];
                            //  System.out.println("current_test_case for else="+current_test_case);//me
                        }

                    }
                    
                    
                    current_test_case = sbuf.toString();
                    sbuf.setLength(0);
                    int w = check_weight(current_test_case, this.interaction_element_list);
//                    System.out.println("The value of w near cheak_weight for first if ="+w);
                    if (w == max_weight) {
                        
                        this.final_test_suite_list.add(current_test_case);
       //   System.out.println("the value of added weight="+(check_weight (current_test_case, interaction_element_list)));
                        //System.out.println("the value of current test case for adding ="+current_test_case);
                        remove_interaction(current_test_case, this.interaction_element_list);
                        stoping_condition = stoping_condition + max_weight;
                        //System.out.println("the array size is ="+interaction_element_list.size());

                        continue;
                    } else if (w == 0) {
                        // zero_weight=w;
                        continue;
                    } else if (w < previous_weight) {

                        //LBest=swarm1[particle ?];=previous test case;
                        current_test_case = previous_test_case;
                        //    System.out.println("the value of current test case inside first else loop="+current_test_case);

                        StringTokenizer s = new StringTokenizer(previous_test_case, ":");
                        int k = 0;
                        while (s.hasMoreTokens()) {
                    //assign eahc parameter values to an index of k in
                            //in the data array
                            LBest[k] = Integer.parseInt(s.nextToken());
                            // System.out.println("the value of data[0]"+data[k]);//me
                            k++;
                        }
                        // zero_weight=1;
                    } else if (w > previous_weight) {
                        previous_weight = w;
                        previous_test_case = current_test_case;
                        //System.out.println("the value of current test case inside second else loop="+current_test_case);
                        ///////////////////////////////////////
                        // change the LBest to current_test_case
                        ///////////////////////////////////////
                        StringTokenizer s = new StringTokenizer(current_test_case, ":");
                        int k = 0;
                        while (s.hasMoreTokens()) {
                    //assign eahc parameter values to an index of k in
                            //in the data array
                            LBest[k] = Integer.parseInt(s.nextToken());
                            // System.out.println("the value of data[0]"+data[k]);//me
                            k++;
                        }
                    }
                }
//update rool
                /**
                 * ***********************************************************************
                 */
//System.out.println("here we enter the update loop, the value of LBest="+current_test_case);
                for (int i = 0; i < particle_size; i++) {
                    for (int j = 0; j < data.length; j++) {
                        /*make a cyclic walk for pBest if we reach the end of the particles
                         going back to the first particle*/
                        if (i == (particle_size - 1)) {
                            velocity[i][j] = (0.3 * velocity[i][j]) + (1.375 * Math.random() * (swarm1[0][j] - swarm1[i][j]) + 1.375 * Math.random() * (LBest[j] - swarm1[i][j]));
                        } else {
                            velocity[i][j] = (0.3 * velocity[i][j]) + (1.375 * Math.random() * (swarm1[i + 1][j] - swarm1[i][j]) + 1.375 * Math.random() * (LBest[j] - swarm1[i][j]));
                        }
                        Math.round(velocity[i][j]);// round the velocity to the nearest iniger.
                        int x = (int) velocity[i][j]; // convert the velocity to an integer from couble to add with the integer position.
                        swarm1[i][j] = Math.abs((swarm1[i][j]) + (x)); // add the velosity to the position to find the new position.
                        if (swarm1[i][j] >= data[j]) {// if the positon is bigger than a sertain value return to the first value.
                            swarm1[i][j] = 0;
                        }

                    }
                }
//for (int i=0;i<particle_size;i++){
                //for (int j=0;j<data.length;j++){
                //   System.out.print(" "+swarm1[i][j]);
                // }
                //   System.out.println();
                // }
//            System.out.println("the value of iteration="+iteration);

                /**
                 * ***********************************************************************
                 */
                iteration++;
                if (iteration > Max_iteration) {
                    stop = true;
                }
            }//end first whhile loop
            
            
            int ww = check_weight(current_test_case, this.interaction_element_list);
            
            System.out.println("Current Test Case: "+current_test_case +" Weiht: "+ww);
            
            if (ww == 0) {
                System.out.println("Continue: ww= " +ww);
                continue;
            } else {
                 System.out.println("the value of added weight="+(check_weight (current_test_case, this.interaction_element_list)));
//System.out.println("the value of count = "+count);
                final_test_suite_list.add(current_test_case);
//                System.out.println("the value of current test case for adding ="+current_test_case);
                stoping_condition = stoping_condition + ww;
                remove_interaction(current_test_case, this.interaction_element_list);
// stoping_condition=stoping_condition+(check_weight (current_test_case, interaction_element_list));
// System.out.println("the array size is ="+interaction_element_list.size());
            }
        }//end second while loop
        
        System.out.println("end of loop");
        System.out.println("######################################################");
    }//end of function

//*****************************************************************
    //                 CHECK WEIGHT
    //*****************************************************************
    private int check_weight(String test_case,
            ArrayList<String> element_list) {

//        System.out.println("CHECK WEIGHT: test_case="+test_case +" Element "+element_list.size());
        int weight = 0;
        int i = 0;

   // System.out.println("INdexing_Arraylist= "+Indexing_Array);
        //for (int m=0;m<Indexing.length;m++){
        //   System.out.println("<<<<<<<<<<<the valuue of indexing matrix is >>>>>>>>"+Indexing[m]);
        //}
        
        String covered_interaction[] = new String[this.binary_cmd_list.size()];
        // break test case into parts
        for (int it = 0; it < this.binary_cmd_list.size(); it++) {
            String s = this.binary_cmd_list.get(it);  // Downcasting is required pre Java 5.
            String s_val = "";
            String r_val = "";
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == '0') {
                    s_val = "-1";
                } else {
                    String[] result = test_case.split(":");
                    for (int x = 0; x < result.length; x++) {
                        if (x == j) {
                            s_val = "" + result[x];
                        }
                    }
                }
                if (j < (s.length() - 1)) {
                    r_val = r_val + s_val + ":";
                } else {
                    r_val = r_val + s_val;
                }
            }
            covered_interaction[i] = r_val;
            //System.out.println (covered_interaction[i]);
            i++;
        }
        //  System.out.println("binary cmd list  = "+binary_cmd_list);
        for (int z = 0; z < covered_interaction.length; z++) {////////////////////////////////////////////

     //   System.out.println("the length of covered_interaction = "+covered_interaction.length);
            // System.out.println("the value of covered_interaction = "+covered_interaction[z]);
            //System.out.println("the length of Indexing_Array = "+Indexing_Array.get(z));
            // System.out.println("binary cmd list  = "+binary_cmd_list);
            //  System.out.println("(Integer)(Indexing_Array.get(z+1)) = "+(Integer)(Indexing_Array.get(z)));
            if (z == 0) {
                //int value=(Integer)(Indexing_Array.get(z+1));
                for (int it = 0; it < (Integer) (this.Indexing_Array.get(0)); it++) {
                    // System.out.println("ititititititititititit>>>>>>>>>>>>>>>>>>>>>>>>>>>>= "+it);

                    String s = (String) element_list.get(it);  // Downcasting is required pre Java 5.
                    if (covered_interaction[z].equals(s)) {
                        weight++;
                    }
           //  System.out.println(" interaction_element_list.get(it)="+ interaction_element_list.get(it));
                    // System.out.println(" weight++="+ weight);
                }
            } else if (z > 0) {

                int value = (Integer) (this.Indexing_Array.get(z - 1));

                for (int it = value; it < (Integer) (this.Indexing_Array.get(z)); it++) {
//                      System.out.println("it >= "+it);
                    String s = (String) element_list.get(it);  // Downcasting is required pre Java 5.
                    if (covered_interaction[z].equals(s)) {
                        weight++;
                    }
            // System.out.println(" weight+++++++++++++++++22222++++++++++++="+ weight);
//                       System.out.println(" interaction_element_list.get(it)="+ interaction_element_list.get(it));
                }
            }
        }

        //System.out.println(weight);
        return (weight);
    }
//*****************************************************************
    //                 REMOVE INTERACTION
    //*****************************************************************

    private void remove_interaction(String test_case,
            ArrayList<String> interaction_element_list) {
        int i = 0;
        String covered_interaction[] = new String[binary_cmd_list.size()];
        // break test case into parts
        for (Iterator it = binary_cmd_list.iterator(); it.hasNext();) {
            String s = (String) it.next();  // Downcasting is required pre Java 5.
            String s_val = "";
            String r_val = "";
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == '0') {
                    s_val = "-1";
                } else {
                    String[] result = test_case.split(":");
                    for (int x = 0; x < result.length; x++) {
                        if (x == j) {
                            s_val = "" + result[x];
                        }
                    }
                }
                if (j < (s.length() - 1)) {
                    r_val = r_val + s_val + ":";
                } else {
                    r_val = r_val + s_val;
                }
            }
            covered_interaction[i] = r_val;
            //System.out.println (covered_interaction[i]);
            i++;
        }
        // System.out.println("covered interaction element length>>>>>>>>>>>>>>>>>>>>>>>>>"+covered_interaction.length);

        for (int z = 0; z < covered_interaction.length; z++) {
            if (z == 0) {
                //int value=(Integer)(Indexing_Array.get(z+1));
                for (int it = 0; it < (Integer) (this.Indexing_Array.get(0)); it++) {

                    String s = (String) interaction_element_list.get(it);  // Downcasting is required pre Java 5.
                    if (s == "*") {
                        continue;
                    } else if (covered_interaction[z].equals(s)) {
                        interaction_element_list.set(it, "*");
                    }

            // System.out.println(" interaction_element_list.get(it)="+ interaction_element_list.get(it));
                }
            } else if (z > 0) {

                int value = (Integer) (this.Indexing_Array.get(z - 1));

                for (int it = value; it < (Integer) (this.Indexing_Array.get(z)); it++) {
                    // System.out.println("ititititititititititit>>>>>>>>>>>>>>>>>>>>>>>>>>>>= "+it);
                    String s = (String) interaction_element_list.get(it);  // Downcasting is required pre Java 5.
                    if (covered_interaction[z].equals(s)) {
                        interaction_element_list.set(it, "*");
                    }

                    // System.out.println(" interaction_element_list.get(it)="+ interaction_element_list.get(it));
                }
                //  interaction_element_list.remove(covered_interaction[z]);
            }
        }
    //display_list ("Interaction Element List (before generation)",interaction_element_list);
//stoping_condition++;
    }

}
