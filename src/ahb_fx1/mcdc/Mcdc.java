/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahb_fx1.mcdc;

import java.util.ArrayList;
import java.util.Random;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author ariful
 */
public class Mcdc {

//    Store result of each loop
//    private ArrayList <ArrayList>    fr              = new ArrayList<ArrayList>();
    private ArrayList <String> finalResult      = new ArrayList<String>();
    private ArrayList <String> result           = new ArrayList<String>();
    private ArrayList <String> variable_list    = new ArrayList<String>();
    private ArrayList <String> marker_list      = new ArrayList<String>();
    private ArrayList <String> MCDC_list        = new ArrayList<String>();



    private int no_of_var = 0;
    private String expression;
    private int MCDC_bit_position=0;
    private int MCDC_not_exist_limit = 100000;
//    private int count=0;


    private ArrayList expresult = new ArrayList();


    public void initialize(String argExpression)
    {

        this.expression = argExpression;

             // taboo list to keep track of repetition of variables
      // variable cannot be negating each other in an expression
      ArrayList <String> taboo_list=new ArrayList<String>();

     // save the variable list
        for (int i = 0; i < argExpression.length(); i++)
        {
            if (Character.isLetter(argExpression.charAt(i)))
            {
             if (!taboo_list.contains(Character.toString(argExpression.charAt(i))))
              {
               this.no_of_var++;

               this.variable_list.add(Character.toString(argExpression.charAt(i)));
               taboo_list.add(Character.toString(argExpression.charAt(i)));
              }
            }

        }


    }//end function

    public void setMcdcExitLimit(Integer value){
        this.MCDC_not_exist_limit = value;
    }

    public final ArrayList<String> searchGD(Integer itr_size, Double final_lbl)
    {
        int count =0;
        this.marker_list.clear();

//        System.out.println("GD Expression: "+this.expression);
        //Variables
        ArrayList<String> localMcdc        = new ArrayList<String>();

        int iter = itr_size;
        double initial_level = (double) Math.pow(2, this.no_of_var);
        double final_level = final_lbl;
        double delta_level= (initial_level-final_level)/iter;
//        System.out.println("*************************");
//        System.out.println ("Great Deluge Parameters - Releasing water from DAM Model");
//        System.out.println ("Initial level = "+initial_level);
//        System.out.println ("Delta level ="+delta_level);
//        System.out.println("*************************");
//        System.out.println("Marker list: "+this.marker_list.size() + " no of var: "+this.no_of_var);
        while (marker_list.size()<no_of_var)
        {
          String current=generate_random_binary();
//          System.out.println ("Current Value => "+current);
//          System.out.println ("---------------------------------");

          int index=0;
          double level=initial_level;

          while (index<iter && level>0.0001)
           {
             String neighbor= generate_neighbor (current);
             if (check_MCDC_pair(this.expression,neighbor,current))
              {

                String var_name = variable_list.get(MCDC_bit_position);
//                System.out.println ("Covering Variable = "+var_name);
//                System.out.println ("Current Value  = "+current);
//                System.out.println ("Neighbour Value = "+neighbor);

                if (!marker_list.contains(var_name))
                {
//                  System.out.println ("Adding to the MCDC final list");
                  marker_list.add(var_name);
//                  if (!MCDC_list.contains(current))
//                    MCDC_list.add (current);
//                  if (!MCDC_list.contains(neighbor))
//                   MCDC_list.add (neighbor);

                    if (!localMcdc.contains(current))
                        localMcdc.add (current);
                    if (!localMcdc.contains(neighbor))
                        localMcdc.add (neighbor);

                }//end if

              }

             index++;
             level = level - delta_level;
             count++;
             if (count>this.MCDC_not_exist_limit)
              {
//                System.out.println ("MCDC pair does not exist");
//                System.exit(0);
//                return 0;
                return localMcdc;
              }
//             System.out.println ("Level Update ="+level);
//             System.out.println ("Loop Index = "+index);
//             System.out.println ("---------------------------------");
           }  // index < 10
        } // var list is empty


//        return localMcdc.size();
        return localMcdc;

    }//end function

    public final ArrayList<String> searchHC(Integer iterator)
    {
        int count =0;
        this.marker_list.clear();
        ArrayList<String> localMcdc        = new ArrayList<String>();

//        System.out.println("HC Expression: "+this.expression);

        int iter=iterator;
//      System.out.println("*************************");
//      System.out.println ("Hill Climbing");
//      System.out.println ("Iteration  = "+iter);
//      System.out.println("*************************");

      while (this.marker_list.size()<this.no_of_var)
      {
        String current=generate_random_binary();
//        System.out.println ("Current Value => "+current);
//        System.out.println ("---------------------------------");

        int index=0;
        while (index<iter)
         {
           String neighbor= generate_neighbor (current);
           if (check_MCDC_pair(this.expression,neighbor,current))
            {

              String var_name = variable_list.get(MCDC_bit_position);
//              System.out.println ("Covering Variable = "+var_name);
//              System.out.println ("Current Value  = "+current);
//              System.out.println ("Neighbour Value = "+neighbor);

              if (!marker_list.contains(var_name))
              {
//                System.out.println ("Adding to the MCDC final list");
                marker_list.add(var_name);
                if (!localMcdc.contains(current))
                  localMcdc.add (current);
                if (!localMcdc.contains(neighbor))
                 localMcdc.add (neighbor);

                current=neighbor;
              }

            }

           index++;
           count++;
           if (count>this.MCDC_not_exist_limit)
            {
//              System.out.println ("MCDC pair does not exist");
//              System.exit(0);
//                return 0;
                return localMcdc;
            }
//           System.out.println ("Loop Index = "+index);
//           System.out.println ("---------------------------------");
         }  // index < 10
      } // var list is empty

//      return localMcdc.size();
      return localMcdc;

    }//END search HC

    public final ArrayList<String> searchSA(Double init_temp, Double clRate)
    {
        int count =0;

        this.marker_list.clear();

        //Variables
        ArrayList<String> localMcdc        = new ArrayList<String>();

//        double initial_temperature =10000.00;
        double initial_temperature = init_temp;
        double temperature = initial_temperature;
//        double coolingRate = 0.01;
        double coolingRate = clRate;

//        System.out.println("SA Expression: "+this.expression);
//        System.out.println("*************************");
//        System.out.println ("Simulated Annealing");
//        System.out.println ("Temperature  = "+temperature);
//        System.out.println ("Cooling Rate  = "+coolingRate);
//        System.out.println("*************************");

//        System.out.println("Marker list: "+this.marker_list.size() + " no of var: "+this.no_of_var);
      while (this.marker_list.size()< this.no_of_var)
      {
        String current=generate_random_binary();
//        System.out.println ("Current Value => "+current);
//        System.out.println ("---------------------------------");

        temperature = initial_temperature; //reheat

        while (temperature>0.0000000001) // start simulated annealing
         {
           String neighbor= this.generate_neighbor (current);
           int currentEnergy = Integer.parseInt(current, 2);
           int neighborEnergy =Integer.parseInt(neighbor, 2);

           if (this.check_MCDC_pair(this.expression,neighbor,current)) //Check energy
            {

              String var_name = this.variable_list.get(this.MCDC_bit_position);
//              System.out.println ("Covering Variable = "+var_name);
//              System.out.println ("Current Value  = "+current);
//              System.out.println ("Neighbour Value = "+neighbor);
//                System.out.println("Var Name: "+ var_name);
              if (!this.marker_list.contains(var_name))
              {
//                System.out.println ("Adding to the MCDC final list");
                this.marker_list.add(var_name);

                if (!localMcdc.contains(current))
                  localMcdc.add (current);
                if (!localMcdc.contains(neighbor))
                 localMcdc.add (neighbor);

//                if (!MCDC_list.contains(current))
//                  MCDC_list.add (current);
//                if (!MCDC_list.contains(neighbor))
//                 MCDC_list.add (neighbor);

              }

            }
           else
           {
              int deltaEnergy = currentEnergy-neighborEnergy;
              double probability = Math.exp(-deltaEnergy/temperature);

              Random rndm = new Random();
              float r =rndm.nextFloat(); // generate random number r

              if (probability>r)
                {
                   current = neighbor;
//                   System.out.println ("[Select with Probability] = "+
//                    probability + " > [r] = "+r);
                }
           }

           count++;
           if (count>this.MCDC_not_exist_limit)
            {
//              System.out.println ("MCDC pair does not exist");
//              System.exit(0);
//              return 0;
                return localMcdc;
            }

//           System.out.println ("Current Temperature = "+temperature);
//           System.out.println ("---------------------------------");
           temperature *=coolingRate;

         }  // temperature controlled
      } // var list is empty

//        return localMcdc.size();
      return localMcdc;

    }//END search SA

    public final ArrayList<String> searchLAHC(Integer itr_size, Integer m_size)
    {
        int count =0;

        this.marker_list.clear();

        //Variables
        ArrayList<String> localMcdc        = new ArrayList<String>();
//        System.out.println("LAHC Expression: "+this.expression);


     ArrayList<String> history_memory = new ArrayList<String>();
     int memory_size = m_size;
     int loop;
     int iteration = itr_size;
     String current;

//     System.out.println("*************************");
//     System.out.println ("Late Acceptance Hill Climbing");
//     System.out.println ("Memory Size  = "+memory_size);
//     System.out.println ("Iteration  = "+iteration);
//     System.out.println ("No of Var: "+no_of_var);
//     System.out.println("*************************");

     while (marker_list.size()<no_of_var)
      {

        // clear the memory contents for next iteration
        history_memory.clear();

       // store the solution instead of its
       // objective value
       loop=0;
       while (loop<memory_size)
         {
          current=generate_random_binary();
          history_memory.add(current);
          loop++;
         }

       current=generate_random_binary();
       int index = 0;
       boolean complete=false;

       while (!complete)
        {
//            @todo current = generate_neighbor(current);
          current = generate_neighbor(current);
//          System.out.println ("Current Value => "+current);
//          System.out.println ("---------------------------------");

          // check the past objective value
          int v = index % memory_size;
          String memory = history_memory.get(v);
          if (check_MCDC_pair(this.expression,memory,current))
           {
              String var_name = variable_list.get(MCDC_bit_position);
//              System.out.println ("Covering Variable = "+var_name);
//              System.out.println ("Current Value  = "+current);
//              System.out.println ("Memory Value = "+memory);

              if (!this.marker_list.contains(var_name))
              {
//                System.out.println ("Adding to the MCDC final list");
                this.marker_list.add(var_name);
                if (!localMcdc.contains(current))
                  localMcdc.add (current);
                if (!localMcdc.contains(memory))
                 localMcdc.add (memory);
              }else{
//                  System.out.println("Not a pair");
              }
//              System.out.println("------------------------------");

            }

           count++;
           if (count>this.MCDC_not_exist_limit)
            {
//              System.out.println ("MCDC pair does not exist");
//              System.out.println("==========================");

//                display_list("Order of Var Name Covered",marker_list);
//                display_list("MCDC Pairs", MCDC_list);

//              System.exit(0);
//                return 0;
                return localMcdc;
            }

           if (index>=iteration)
             {
              complete=true;
              break;
             }
           index++;
        }
      }

//        return localMcdc.size();
     return localMcdc;

    }//END search LAHC


    /*
    public void runLoop(String argExpression, Integer loopLimit)
    {
//        this.expression = argExpression;
        System.out.println("Expression: "+argExpression +" LoopLimit: "+loopLimit);

        this.expression = argExpression;
        this.initialize(this.expression);

        for (int i=0; i<loopLimit ;i++)
        {
            ArrayList ar = new ArrayList();
//            0. Start time
            long startTime = System.nanoTime();

//            1.1 Run GD which result MCDC Pair result
            Integer mcdcSize = this.searchGD();
//            Integer mcdcSize = 3;

//            1.2 end time
            long endTime = System.nanoTime();

//            2. Calculate Time
            double seconds = (double)(endTime - startTime) / 1000000000.0;
            NumberFormat formatter = new DecimalFormat("#0.00");
//            System.out.println("Execution time is " + formatter.format((seconds)) + " seconds");


//            3. Prepare row result array [sn, seq, algorithm, mcdcsize, time];
                ar.add(i);
                ar.add(1);
                ar.add("GD");
                ar.add("Pairs: "+mcdcSize);
                ar.add(formatter.format((seconds)));

            this.finalResult.add(ar.toString());
            //add the result with sn, seq, algorithm name, time, mcdc pair size


        }//end for

        System.out.println(this.finalResult);


    }//end function

    */


    //supporting function


 /**
  *
  * Check if pair of expression is MCDC pair
  * and mark the last MCDC bit position of affected variable*
  *
  * @param expression
  * @param binary1 neighbour solution
  * @param binary2 current solution
  * @return outcome boolean
  */
 private boolean check_MCDC_pair (String argExpression,
                                    String binary1,
                                    String binary2)
   {
      boolean outcome =true;

//      System.out.println("Check mcdc pair: "+argExpression);
      int length =no_of_var;
      String mresult="";
      MCDC_bit_position = -1;

      // check that output must be different for MCDC compliant
      if (evaluate_boolean_expression(argExpression, binary1)==
          evaluate_boolean_expression(argExpression, binary2))
      {
         outcome=false;
      }
      else
      {
         int count=0;
         for (int i=0;i<no_of_var;i++)
         {
           if (binary1.charAt(i)== binary2.charAt(i))
              mresult=mresult + Character.toString(binary1.charAt(i));
           else
           {
              mresult=mresult+"*";
              MCDC_bit_position=i;
              count++;
           }
         }
         // MCDC pair can change only in 1 bit
         if (count>1)
            outcome=false;
      }

     return outcome;
   }//end function


  ///////////////////////////////////////////////////////
  // generate random binary number based on no of var
  ///////////////////////////////////////////////////////
 private String generate_random_binary ()
    {


       int max_number = (int) Math.pow(2,no_of_var);
       Random t = new Random();
       int random_no = t.nextInt(max_number);
       String comb = new String();
       comb=Integer.toBinaryString(random_no);
       while (comb.length()<no_of_var)
           comb ="0"+comb;

       return (comb);

    }//end function

  ///////////////////////////////////////////////////////
  // generate neighbor randonmly by changing 1 bit at a time
  ///////////////////////////////////////////////////////
  private String generate_neighbor (String binary_value)
    {
       Random random =new Random();
       int index = random.nextInt(binary_value.length()); // range 0 till length of binary ...

       if (binary_value.charAt(index)=='0')
          binary_value = replace (binary_value,index,'1');
       else
          binary_value = replace (binary_value,index,'0');


       return (binary_value);

    }//end function

  ///////////////////////////////////////////////////
  // Replace a string with specific char at a given index
  // used by generate_neighbor
  ////////////////////////////////////////////////////
   private String replace(String str, int index, char ch)
    {
     if(str==null)
      {
        return str;
      }else if(index<0 || index>=str.length())
      {
        return str;
      }
    char[] chars = str.toCharArray();
    chars[index] = ch;
    return String.valueOf(chars);
   }

  ///////////////////////////////////////////////////
 //  Evaluate Boolean Expression
 //      -no of variable must be established first
 //       and match with length of binary value
 //////////////////////////////////////////////////
 private boolean evaluate_boolean_expression (String argExpression,
                                              String binary_value){
          String tempinput ="";

          boolean ret_result=false;
          tempinput = argExpression;

//          System.out.println("Evaluate Boolean Expression: "+tempinput + "Binary Value: "+binary_value);

          for(int f=0; f< no_of_var; f++)
           {
                char[] testchar = binary_value.toCharArray();
                // rewrite expression by replacing variable
                tempinput = tempinput.replaceAll(variable_list.get(f),
                Character.toString(testchar[f]));
            }

          ScriptEngine engine = new
            ScriptEngineManager().getEngineByExtension("js");
            try
              {
                // Evaluate the expression
                Object result = engine.eval(tempinput);

                // convert object to string
                String s = result.toString();
                s = s.substring(0,1);

                if (s.equals("0"))
                   ret_result=false;
                 else if(s.equals("1"))
                   ret_result=true;


               }
              catch (ScriptException e1)
               {
                 e1.printStackTrace();
               }

           return ret_result;

    }//end function




}//end class
