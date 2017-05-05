// The "PL_NSpire" class.
import java.awt.*;
import hsa.Console;
import javax.imageio.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.text.*;
import java.util.*;
import java.lang.Object;

public class LP_MC_FN_NSpire
{
    static Console c;           // The output console
    static char[] eq;
    static double xmin, xmax, ymin, ymax, increment, ans;
    static int[] xPixels, yPixels;
    static double[] xValues, yValues;
    static char choice;
    static int lnCount, placeValue;
    static boolean error;
    //StatTab variables (linear)
    public static ArrayList x = new ArrayList ();
    public static ArrayList y = new ArrayList ();
    public static double[] xArray;
    public static double[] yArray;
    public static double[] xyArray;
    public static double[] xsqArray;
    public static double[] ysqArray;
    public static double xdoubleparse, ydoubleparse;
    //StatTab variables (exponential)
    public static double[] ylogArray;
    //StatTab variables (quadratic)
    public static double[] xcubeArray;
    public static double[] xtesseractArray;
    public static double[] xsqyArray;
    public static double matrix0[] [] = new double [3] [4];
    public static double matrix1[] [] = new double [3] [4];
    public static double matrix2[] [] = new double [3] [4];
    public static double matrix3[] [] = new double [3] [4];
    public static double matrix4[] [] = new double [3] [4];
    public static double matrix5[] [] = new double [3] [4];
    public static double matrix6[] [] = new double [3] [4];
    public static double[] sseArray;
    public static double[] sstArray;

    //****************************Parses and calculates an equation*****************************
    //First major method: converts a char array (the equation) into a string array which works better
    public static String[] makeInput (char[] eq, double x)
    {
	String[] input = new String [eq.length];
	int count = 0;
	try
	{
	    //Goes through all the chars
	    for (int i = 0 ; i < eq.length ; i++)
	    {
		//Groups numbers together (e.g. 2,3,5,.,7 (5 separate elements) becomes 235.7 (one element)
		if (java.lang.Character.isDigit (eq [i]) == true || eq [i] == '.' && java.lang.Character.isDigit (eq [i - 1]) == false && eq [i - 1] != '.')
		{
		    String num = "";
		    while (java.lang.Character.isDigit (eq [i]) == true || eq [i] == '.')
		    {
			num = num + eq [i];
			i++;
			if (i == eq.length)
			{
			    break;
			}
		    }
		    i--;
		    input [count] = num;
		    count++;
		}
		//Groups functions together (e.g. s,i,n (3 separate elements) becomes sin (one element)
		else if (eq [i] == 's' && eq [i + 1] == 'i' && eq [i + 2] == 'n')
		{
		    i = i + 2;
		    input [count] = "sin";
		    count++;
		}
		else if (eq [i] == 'c' && eq [i + 1] == 'o' && eq [i + 2] == 's')
		{
		    i = i + 2;
		    input [count] = "cos";
		    count++;
		}
		else if (eq [i] == 't' && eq [i + 1] == 'a' && eq [i + 2] == 'n')
		{
		    i = i + 2;
		    input [count] = "tan";
		    count++;
		}
		else if (eq [i] == 'l' && eq [i + 1] == 'n')
		{
		    i = i++;
		    input [count] = "ln";
		    count++;
		}
		else if (eq [i] == 's' && eq [i + 1] == 'q' && eq [i + 2] == 'r' && eq [i + 3] == 't')
		{
		    i = i + 3;
		    input [count] = "sqrt";
		    count++;
		}
		else if (eq [i] == 'n' && eq [i + 1] == 'e' && eq [i + 2] == 'g')
		{
		    i = i + 2;
		    input [count] = "neg";
		    count++;
		}
		//Replaces expressions like x or pi with their numerical values
		else if (eq [i] == 'x')
		{
		    input [count] = Double.toString (x);
		    count++;
		}
		else if (eq [i] == 'p' && eq [i + 1] == 'i')
		{
		    i = i + 1;
		    input [count] = String.valueOf (3.1415926535897932);
		    count++;
		}
		else if (eq [i] == 'e')
		{
		    input [count] = String.valueOf (2.7182818284590452);
		    count++;
		}
		else if (eq [i] == 'a' && eq [i + 1] == 'n' && eq [i + 2] == 's')
		{
		    i = i + 2;
		    input [count] = String.valueOf (ans);
		    count++;
		}
		//Everything else is left untouched (e.g. +, -)
		else
		{
		    input [count] = Character.toString (eq [i]);
		    count++;
		}
	    }
	}
	//Identifies indexoutofbounds exceptions (which are generated from invalid equations)
	catch (IndexOutOfBoundsException e)
	{
	    error = true;
	}
	finally
	{
	    return input;
	}
    }


    //Second major method: converts string array from makeInput into reverse Polish notation using the shunting yard algorithm
    //http://en.wikipedia.org/wiki/Shunting-yard_algorithm#The_algorithm_in_detail
    //Essentially I followed the instructions from the above link
    //The following comments are mostly copy-pasted from the link
    public static String[] reversePolish (String[] input)
    {
	String[] output = new String [input.length];
	String[] operator = new String [input.length];
	int outputCount = 0;
	int operatorCount = 0;
	//Read a token
	for (int i = 0 ; i < input.length ; i++)
	{
	    if (input [i] != null && input [i] != "")
	    {
		//If the token is a number, then add it to the output queue
		if (identifier (input [i]) == "number")
		{
		    output [outputCount] = input [i];
		    outputCount++;
		}
		//If the token is a function token, then push it onto the stack
		else if (identifier (input [i]) == "function")
		{
		    operator [operatorCount] = input [i];
		    operatorCount++;
		}
		//If the token is an operator o1
		else if (identifier (input [i]) == "operator")
		{
		    //push o1 onto the stack
		    if (operatorCount == 0)
		    {
			operator [operatorCount] = input [i];
			operatorCount++;
		    }
		    //while there is an operator token, o2, at the top of the stack
		    else
		    {
			//o1 is left-associative and its precedence is *less than or equal* to that of o2
			if (input [i].equals ("^") == false)
			{
			    while (operatorCount != 0 && orderOps (operator [operatorCount - 1]) >= orderOps (input [i]))
			    {
				output [outputCount] = operator [operatorCount - 1];
				outputCount++;
				operator [operatorCount - 1] = null;
				operatorCount--;
			    }
			    //push o1 onto the stack
			    operator [operatorCount] = input [i];
			    operatorCount++;
			}
			//o1 if right associative, and has precedence *less than* that of o2
			else
			{
			    while (operatorCount != 0 && orderOps (operator [operatorCount - 1]) > orderOps (input [i]))
			    {
				output [outputCount] = operator [operatorCount - 1];
				outputCount++;
				operator [operatorCount - 1] = null;
				operatorCount--;
			    }
			    //push o1 onto the stack
			    operator [operatorCount] = input [i];
			    operatorCount++;
			}
		    }
		}
		else if (identifier (input [i]) == "bracket")
		{
		    //If the token is a left parenthesis, then push it onto the stack
		    if (input [i].equals ("(") == true)
		    {
			operator [operatorCount] = input [i];
			operatorCount++;
		    }
		    //If the token is a right parenthesis
		    else
		    {
			//Until the token at the top of the stack is a left parenthesis, pop operators and functions off the stack onto the output queue
			while (operatorCount != 0 && operator [operatorCount - 1].equals ("(") == false)
			{
			    output [outputCount] = operator [operatorCount - 1];
			    outputCount++;
			    operator [operatorCount - 1] = null;
			    operatorCount--;
			}
			//Pop the left parenthesis from the stack, but not onto the output queue
			if (operatorCount != 0 && operator [operatorCount - 1].equals ("(") == true)
			{
			    operator [operatorCount - 1] = null;
			    operatorCount--;
			}
		    }
		}
	    }
	}
	//When there are no more tokens to read
	//While there are still operator tokens in the stack
	for (operatorCount = operatorCount ; operatorCount > 0 ; operatorCount--)
	{
	    //Pop the operator onto the output queue
	    output [outputCount] = operator [operatorCount - 1];
	    outputCount++;
	    operator [operatorCount - 1] = null;
	}
	return output;
    }


    //Third major method: evaluates the reverse Polish notation generated by reversePolish
    //http://en.wikipedia.org/wiki/Reverse_Polish_notation#Postfix_algorithm
    //Essentially I followed the instructions from the above link
    //The following comments are mostly copy-pasted from the link
    public static double evaluate (String[] output)
    {
	double[] result = new double [output.length];
	int count = 0;
	try
	{
	    //Read the next token from input
	    for (int i = 0 ; i < output.length ; i++)
	    {
		if (output [i] != null)
		{
		    //If the token is a value
		    if (identifier (output [i]) == "number")
		    {
			//Push it onto the stack
			result [count] = Double.parseDouble (output [i]);
			count++;
		    }
		    //If the token is a function (i.e. an operation with one argument)
		    //Pop the top value
		    //Evaluate the operator on the top value
		    //Push the returned result back onto the stack
		    else if (identifier (output [i]) == "function")
		    {
			if (output [i].equals ("sin") == true)
			{
			    result [count - 1] = Math.sin (result [count - 1]);
			}
			else if (output [i].equals ("cos") == true)
			{
			    result [count - 1] = Math.cos (result [count - 1]);
			}
			else if (output [i].equals ("tan") == true)
			{
			    result [count - 1] = Math.tan (result [count - 1]);
			}
			else if (output [i].equals ("ln") == true)
			{
			    result [count - 1] = Math.log (result [count - 1]);
			}
			else if (output [i].equals ("sqrt") == true)
			{
			    result [count - 1] = Math.sqrt (result [count - 1]);
			}
			else if (output [i].equals ("neg") == true)
			{
			    result [count - 1] = -(result [count - 1]);
			}
		    }
		    //If the token is an operation (i.e. has two arguments)
		    //Pop the top two values
		    //Evaluate the operator on the top two values
		    //Push the returned result back onto the stack
		    else if (identifier (output [i]) == "operator")
		    {
			if (output [i].equals ("+") == true)
			{
			    result [count - 2] = result [count - 1] + result [count - 2];
			    count--;
			}
			else if (output [i].equals ("-") == true)
			{
			    result [count - 2] = result [count - 2] - result [count - 1];
			    count--;
			}
			else if (output [i].equals ("*") == true)
			{
			    result [count - 2] = result [count - 1] * result [count - 2];
			    count--;
			}
			else if (output [i].equals ("/") == true)
			{
			    result [count - 2] = result [count - 2] / result [count - 1];
			    count--;
			}
			else if (output [i].equals ("^") == true)
			{
			    result [count - 2] = Math.pow (result [count - 2], result [count - 1]);
			    count--;
			}
		    }
		}
	    }
	}
	//Identifies indexoutofbounds exceptions (which are generated from invalid equations)
	catch (IndexOutOfBoundsException e)
	{
	    error = true;
	}
	finally
	{
	    //If there is only one value in the stack, that value is the result of the calculation
	    ans = result [0];
	    return result [0];
	}
    }


    //Sub method - order of operations
    public static int orderOps (String op)
    {
	if (op.equals ("+") == true)
	{
	    return 1;
	}
	else if (op.equals ("-") == true)
	{
	    return 1;
	}
	else if (op.equals ("/") == true)
	{
	    return 2;
	}
	else if (op.equals ("*") == true)
	{
	    return 2;
	}
	else if (op.equals ("^") == true)
	{
	    return 3;
	}
	else if (identifier (op).equals ("function"))
	{
	    return 4;
	}
	else
	{
	    return 0;
	}
    }


    //Sub method - identifies a string
    public static String identifier (String term)
    {
	if (
		term.equals ("sin") == true ||
		term.equals ("cos") == true ||
		term.equals ("tan") == true ||
		term.equals ("ln") == true ||
		term.equals ("sqrt") == true ||
		term.equals ("neg") == true
		)
	{
	    return "function";
	}
	else if (
		term.equals ("+") == true ||
		term.equals ("-") == true ||
		term.equals ("*") == true ||
		term.equals ("/") == true ||
		term.equals ("^") == true
		)
	{
	    return "operator";
	}
	else if (
		term.startsWith ("1") == true ||
		term.startsWith ("2") == true ||
		term.startsWith ("3") == true ||
		term.startsWith ("4") == true ||
		term.startsWith ("5") == true ||
		term.startsWith ("6") == true ||
		term.startsWith ("7") == true ||
		term.startsWith ("8") == true ||
		term.startsWith ("9") == true ||
		term.startsWith ("0") == true ||
		term.startsWith (".") == true ||
		term.startsWith ("-") == true
		)
	{
	    return "number";
	}
	else if (
		term.equals ("(") == true ||
		term.equals (")") == true
		)
	{
	    return "bracket";
	}
	else
	{
	    return "error";
	}
    }


    //Groups all three major methods together
    public static double calculate (char[] message, double x)
    {
	String[] input = makeInput (message, x);
	String[] output = reversePolish (input);
	return evaluate (output);
    }


    //Displays coordinates
    public static void displayC (double x, double y)
    {
	String output = "(" + x + "," + y + ")";
	c.setColor (Color.black);
	Font f = new Font ("Arial", Font.PLAIN, 12);
	c.setFont (f);
	//Ensures that the coordinates are placed properly (so that it appears fully on the screen)
	int xshift = -1;
	int yshift = 1;
	if (xPixel (x) > 320)
	{
	    xshift = 1;
	}
	if (yPixel (y) < 300)
	{
	    yshift = -1;
	}
	c.drawString (output, xPixel (x) + xshift * 5, yPixel (y) - yshift * 5);
    }


    //This method is used solely to ensure that the graph window makes sense
    //i.e. xmin<xmax, ymin<ymax
    public static double checkRange (double a, String prompt)
    {
	print (prompt);
	double z = readDouble ();
	for (int i = 0 ;; i++)
	{
	    if (z >= a)
	    {
		break;
	    }
	    else
	    {
		c.print ("Not in range - must be greater than " + a + ". ");
		print (prompt);
		z = readDouble ();
	    }
	}
	return z;
    }


    //This method is used to ensure that inputs for certain functions are within the graph window
    //The graph window is defined by xmin, xmax, ymin, and ymax
    public static double filter (String prompt)
    {
	print (prompt);
	double z = readDouble ();
	for (int i = 0 ;; i++)
	{
	    if (z >= xmin && z <= xmax)
	    {
		break;
	    }
	    else
	    {
		c.print ("Not in graph range. ");
		print (prompt);
		z = readDouble ();
	    }
	}
	return z;
    }


    //Order of magnitude method - used for axes
    //e.g. if the graph window is 100 wide, each axis tick is 10
    public static double OOM (double x)
    {
	x = Math.abs (x);
	double OOM = 0; //to avoid errors
	if (x < 1 && x != 0)
	{
	    x = 1 / x;
	    for (int i = 0 ;; i++)
	    {
		if (Math.pow (10, i) < x && Math.pow (10, i + 1) > x)
		{
		    OOM = Math.pow (10, -i - 1);
		    break;
		}
		else if (Math.pow (10, i) == x)
		{
		    OOM = Math.pow (10, -i - 1);
		    break;
		}
	    }
	}
	else
	{
	    for (int i = 0 ;; i++)
	    {
		if (Math.pow (10, i) < x && Math.pow (10, i + 1) > x)
		{
		    OOM = Math.pow (10, i);
		    break;
		}
		else if (Math.pow (10, i) == x)
		{
		    OOM = Math.pow (10, i - 1);
		    break;
		}
	    }
	}
	return OOM;
    }


    //This method makes sure everything fits in the dialogue box (which is two lines high)
    public static void print (String message)
    {
	c.println (message);
	lnCount++;
	//Resets the dialogue box
	if (lnCount == 2)
	{
	    c.setCursor (1, 1);
	    lnCount = 0;
	}
    }


    //Similar to the print method
    public static int readInt ()
    {
	int output = c.readInt ();
	lnCount++;
	if (lnCount == 2)
	{
	    c.setCursor (1, 1);
	    lnCount = 0;
	}
	return output;
    }


    //Similar to the print method
    public static double readDouble ()
    {
	double output = c.readDouble ();
	lnCount++;
	if (lnCount == 2)
	{
	    c.setCursor (1, 1);
	    lnCount = 0;
	}
	return output;
    }


    //Similar to the print method
    public static String readString ()
    {
	String output = c.readLine ();
	lnCount++;
	if (lnCount == 2)
	{
	    c.setCursor (1, 1);
	    lnCount = 0;
	}
	return output;
    }


    //Displays the graph axes
    //Output is based on xmin, xmax, ymin, ymax (i.e. the graph window)
    public static void axes ()
    {
	//Draws the x and y axis lines
	c.setColor (Color.black);
	int xC = (int) (-(xmin * 640) / (xmax - xmin));
	c.drawLine (xC, 100, xC, 500);
	int yC = 100 + (int) (-(ymax * 400) / (ymin - ymax));
	c.drawLine (0, yC, 640, yC);
	//Ticks
	int OOMX = xPixel (OOM ((xmax - xmin) / 2)) - xPixel (0);
	int OOMY = -yPixel (OOM ((ymax - ymin) / 2)) + yPixel (0);
	c.setColor (Color.black);
	Font f = new Font ("Arial", Font.PLAIN, 12);
	c.setFont (f);
	//Calculates an appropriate tick length
	String numX = Double.toString (OOM ((xmax - xmin) / 2));
	String numY = Double.toString (OOM ((ymax - ymin) / 2));
	//Positive x
	for (int i = xPixel (0) ; i < 640 ; i = i + OOMX)
	{
	    c.drawLine (i, yPixel (0) - 5, i, yPixel (0) + 5);
	    if (i == xPixel (0) + OOMX)
	    {
		c.drawString (numX, i - 5, yPixel (0) + 20);
	    }
	}
	//Negative x
	for (int i = xPixel (0) ; i > 0 ; i = i - OOMX)
	{
	    c.drawLine (i, yPixel (0) - 5, i, yPixel (0) + 5);
	}
	//Positive y
	for (int i = yPixel (0) ; i > 100 ; i = i - OOMY)
	{
	    c.drawLine (xPixel (0) - 5, i, xPixel (0) + 5, i);
	    if (i == yPixel (0) - OOMY)
	    {
		c.drawString (numY, xPixel (0) + 10, i + 5);
	    }
	}
	//Negative y
	for (int i = yPixel (0) ; i < 500 ; i = i + OOMY)
	{
	    c.drawLine (xPixel (0) - 5, i, xPixel (0) + 5, i);
	}
    }


    //*********************************Four crucial methods*****************************
    //Converts a number x into the pixel location in the console
    public static int xPixel (double x)
    {
	int xC = (int) (((x - xmin) * 640) / (xmax - xmin));
	return xC;
    }


    //Converts a number y into the pixel location in the console
    public static int yPixel (double y)
    {
	int yC = 100 + (int) (((y - ymax) * 400) / (ymin - ymax));
	return yC;
    }


    //Converts a pixel location in the console into a number x
    public static double reverseX (int pixel)
    {
	double x = pixel * (xmax - xmin) / 640 + xmin;
	return x;
    }


    //Converts a pixel location in the console into a number y
    public static double reverseY (int pixel)
    {
	double y = (pixel - 100) * (ymin - ymax) / 400 + ymax;
	return y;
    }


    //Loads image from file
    public static Image loadImage (String name)
    {
	Image img = null;
	try
	{
	    img = ImageIO.read (new File (name));
	}
	catch (IOException e)
	{
	}
	return img;
    }


    //The derivative function (the slope at a point)
    public static void derivative (double[] xValues, double[] yValues, double a)
    {
	double output = 0; //to avoid errors
	int k = 0; //to avoid errors
	//Finds the approximate derivative value
	for (int i = 0 ; i < xValues.length ; i++)
	{
	    if (xValues [i] < a && xValues [i + 1] > a)
	    {
		//This expression computes slope by comparing the two stored values closest to the input a
		output = -(yValues [i] - yValues [i + 1]) / increment;
		k = i;
	    }
	}
	//Prints result
	c.print ("The derivative at ");
	c.print (a, 0, 2);
	c.print (" is ");
	c.println (output, 0, 2);
	//Displays the derivative line
	displayC (a, (yValues [k] + yValues [k + 1]) / 2);
	int newA = xPixel (a);
	int newX = xPixel ((xValues [k] + xValues [k + 1]) / 2);
	int newY = yPixel ((yValues [k] + yValues [k + 1]) / 2);
	double b = reverseY (newY) - output * reverseX (newX);
	//Graphs the line using a process similar to the standard graph method
	for (double x = xmin ; x <= xmax ; x = x + increment)
	{
	    double y = x * output + b;
	    int xC = xPixel (x);
	    int yC = yPixel (y);
	    if (yC > 100)
	    {
		c.setColor (Color.red);
		c.drawLine (xC, yC, xPixel (x + increment), yPixel (output * (x + increment) + b));
	    }
	}
    }


    //The zeros function
    public static void zeros (double[] xValues, double[] yValues, double a, double b)
    {
	double output = 0; //to avoid errors
	//Finds the stored y value closest to zero between a and b
	for (int i = 0 ; i < xValues.length - 1 ; i++)
	{
	    if (xValues [i] >= a && xValues [i] <= b)
	    {
		if (yValues [i] < 0 && yValues [i + 1] > 0 || yValues [i] > 0 && yValues [i + 1] < 0)
		{
		    output = -(yValues [i] + yValues [i + 1]) / 2;
		}
		else if (yValues [i] == 0)
		{
		    output = yValues [i];
		}
	    }
	}
	//Prints the result
	c.print ("The zero is ");
	c.println (output, 0, 2);
	//Draws the zero point
	int outputPixel = xPixel (output);
	Color col = new Color (139, 69, 19);
	c.setColor (col);
	c.fillOval (outputPixel - 5, yPixel (0) - 5, 10, 10);
	displayC (output, 0);
    }


    //The maximum function
    public static void max (double[] xValues, double[] yValues, double a, double b)
    {
	double output = yValues [0];
	int x = xPixel (xValues [0]); //to avoid errors
	//Finds the stored y value which is the largest within range
	for (int i = 0 ; i < yValues.length ; i++)
	{
	    if (yValues [i] > output && xValues [i] <= b && xValues [i] >= a)
	    {
		output = yValues [i];
		x = xPixel (xValues [i]);
	    }
	}
	//Prints the result
	c.print ("The maximum from ");
	c.print (a, 0, 2);
	c.print (" to ");
	c.print (b, 0, 2);
	c.print (" is ");
	c.println (output, 0, 2);
	//Draws the maximum point
	c.setColor (Color.green);
	int outputPixel = yPixel (output);
	c.fillOval (x - 5, outputPixel - 5, 10, 10);
	displayC (x, output);
    }


    //The minimum function
    public static void min (double[] xValues, double[] yValues, double a, double b)
    {
	double output = yValues [0];
	int x = xPixel (xValues [0]); //to avoid errors
	//Finds the stored y value which is the smallest within range
	for (int i = 0 ; i < yValues.length ; i++)
	{
	    if (yValues [i] < output && xValues [i] <= b && xValues [i] >= a)
	    {
		output = yValues [i];
		x = xPixel (xValues [i]);
	    }
	}
	//Prints the result
	c.print ("The minimum from ");
	c.print (a, 0, 2);
	c.print (" to ");
	c.print (b, 0, 2);
	c.print (" is ");
	c.println (output, 0, 2);
	//Draws the minimum point
	c.setColor (Color.blue);
	int outputPixel = yPixel (output);
	c.fillOval (x - 5, outputPixel - 5, 10, 10);
	displayC (x, output);
    }


    //Load screen - purely for aesthetic purposes
    //Mimics the actual TI-NSpire load screen
    public static void loadScreen ()
    {
	//Checkerboard pattern
	for (int i = 0 ; i < 32 ; i++)
	{
	    for (int j = 0 ; j < 25 ; j++)
	    {
		if ((i + j) % 2 == 0)
		{
		    c.setColor (Color.red);
		    c.fillRect (i * 20, j * 20, 20, 20);
		}
		else
		{
		    c.setColor (Color.black);
		    c.fillRect (i * 20, j * 20, 20, 20);
		}
	    }
	}
	//The arc-like area at the bottom of the screen
	c.setColor (Color.blue);
	for (int i = 1 ; i <= 400 ; i++)
	{
	    c.drawArc (-200, -200 + i, 900, 600, 180, 180);
	}
	c.setColor (Color.white);
	c.fillRect (215, 235, 210, 30);
	//Displays calculator name
	Font f = new Font ("Courier", Font.BOLD, 60);
	c.setFont (f);
	c.drawString ("FML - NSpire", 100, 140);
	//Fills the loading bar
	c.setColor (Color.black);
	for (int x = 0 ; x <= 200 ; x++)
	{
	    c.fillRect (220, 240, x, 20);
	    try
	    {
		Thread.currentThread ().sleep (20);
	    }
	    catch (Exception e)
	    {
	    }
	}
    }


    //Displays the dialogue box
    public static void box ()
    {
	c.setColor (Color.gray);
	c.fillRect (0, 0, 550, 50);
	c.setColor (Color.white);
	c.fillRect (0, 0, 540, 40);
    }


    //Calculates the integral function (the area between the curve and the x axis)
    public static void integral (double[] xValues, double[] yValues, double a, double b)
    {
	double sum = 0;
	//Sums all the y values in range
	for (int i = 0 ; i < xValues.length ; i++)
	{
	    if (xValues [i] >= a && xValues [i] <= b)
	    {
		sum = sum + yValues [i];
	    }
	}
	//Multiplies by the increment to get area
	sum = sum * increment;
	//Prints the result
	c.print ("The integral from ");
	c.print (a, 0, 2);
	c.print (" to ");
	c.print (b, 0, 2);
	c.print (" is ");
	c.println (sum, 0, 2);
    }


    //Displays the integral as the area between the curve and the x axis
    public static void displayIntegral (int[] xPixels, int[] yPixels, double a, double b)
    {
	Color col = new Color (133, 133, 133);
	c.setColor (col);
	int zero = 100 + (int) (-(ymax * 400) / (ymin - ymax));
	int newA = xPixel (a);
	int newB = xPixel (b);
	//Draws a series of lines from each y value to the point on the x axis directly below it
	//Since each line is so close, it looks like a filled area
	for (int i = 0 ; i < xPixels.length ; i++)
	{
	    if (xPixels [i] >= newA && xPixels [i] <= newB)
	    {
		c.drawLine (xPixels [i], zero, xPixels [i], yPixels [i]);
	    }
	}
    }


    //Graph method - very important
    public static void graph (char[] eq)
    {
	//Public arrays which store all the x and y values which are graphed, as well as their pixel values
	xPixels = new int [(int) (Math.round ((xmax - xmin) / increment + 1))];
	yPixels = new int [(int) (Math.round ((xmax - xmin) / increment + 1))];
	xValues = new double [(int) (Math.round ((xmax - xmin) / increment + 1))];
	yValues = new double [(int) (Math.round ((xmax - xmin) / increment + 1))];
	int count = 0;
	//This section does the graphing
	//Increment is very small (about 1/2000 of the graph window)
	for (double x = xmin ; x <= xmax ; x = x + increment)
	{
	    //Calculates y given x
	    double y = calculate (eq, x);
	    int xC = xPixel (x);
	    int yC = yPixel (y);
	    //Stores values
	    xValues [count] = x;
	    yValues [count] = y;
	    xPixels [count] = xC;
	    yPixels [count] = yC;
	    count++;
	    //Draws a very short line between this coordinate and the next one
	    //The lines are short enough that the final result looks like a curve
	    c.setColor (Color.black);
	    if (yC >= 100)
	    {
		c.drawLine (xC, yC, xPixel (x + increment), yPixel (calculate (eq, x + increment)));
	    }
	}
    }


    //Trace function - given an x value, outputs the y value
    public static void trace (double a)
    {
	//Calculates the y value
	double output = calculate (eq, a);
	//Prints result
	c.print ("The y value at ");
	c.print (a, 0, 2);
	c.print (" is ");
	c.println (output, 0, 2);
	//Draws the point
	int outputPixel = yPixel (output);
	c.setColor (Color.black);
	c.fillOval (xPixel (a) - 5, outputPixel - 5, 10, 10);
	displayC (a, output);
    }


    //This is how the calculator displays its previous four results
    //Input on the left, output on the right
    public static void scrollDisplay (String[] left, String[] right)
    {
	c.setColor (Color.black);
	Font f = new Font ("Arial", Font.PLAIN, 35);
	c.setFont (f);
	//Left side (input)
	for (int i = 4 ; i > 0 ; i--)
	{
	    c.drawString (left [4 - i], 10, 100 * i + 50);
	}
	//Right side (output)
	for (int i = 4 ; i > 0 ; i--)
	{
	    //Shift compensates for the fact that c.drawString draws from the left
	    int shift = right [4 - i].length ();
	    c.drawString (right [4 - i], 640 - shift * 20, 100 * i + 90);
	}
    }


    //A list of backgrounds for each tab
    public static void bg (int i)
    {
	c.clear ();
	lnCount = 0;
	c.setCursor (1, 1);
	//Graphing tab
	if (i == 1)
	{
	    Image bg = loadImage ("graphtab.jpg");
	    c.drawImage (bg, 0, 0, null);
	    box ();
	    axes ();
	}
	//Calculator tab
	else if (i == 2)
	{
	    Image bg = loadImage ("calctab.jpg");
	    c.drawImage (bg, 0, 0, null);
	    box ();
	    c.setColor (Color.black);
	    c.drawLine (0, 200, 640, 200);
	    c.drawLine (0, 300, 640, 300);
	    c.drawLine (0, 400, 640, 400);
	}
	//Stat plot tab
	else if (i == 3)
	{
	    Image bg = loadImage ("stattab.jpg");
	    c.drawImage (bg, 0, 0, null);
	    box ();
	}
	//Library tab, home screen
	else if (i == 4)
	{
	    Image bg = loadImage ("libtab.jpg");
	    c.drawImage (bg, 0, 0, null);
	    box ();
	}
	//Library tab, for each specific page
	else if (i == 5)
	{
	    Image bg = loadImage ("libtab2.png");
	    c.drawImage (bg, 0, 0, null);
	    box ();
	    print ("Press a for previous page, d for next page, any other key to go home.");
	}
    }


    //Prints a file (used for the library tab)
    public static void printFile (String filePath)
    {
	File file = new File (filePath);
	FileInputStream fis = null;
	BufferedInputStream bis = null;
	DataInputStream dis = null;

	try
	{
	    fis = new FileInputStream (file);

	    // Here BufferedInputStream is added for fast reading.
	    bis = new BufferedInputStream (fis);
	    dis = new DataInputStream (bis);

	    Font f = new Font ("Courier", Font.PLAIN, 14);
	    c.setFont (f);
	    c.setColor (Color.black);
	    int lnCount = 1;

	    // dis.available() returns 0 if the file does not have more lines.
	    while (dis.available () != 0)
	    {

		// this statement reads the line from the file and print it to
		// the console.
		String output = dis.readLine ();
		c.drawString (output, 50, 150 + 15 * lnCount);
		lnCount++;
	    }

	    // dispose all the resources after using them.
	    fis.close ();
	    bis.close ();
	    dis.close ();

	}
	catch (FileNotFoundException e)
	{
	    e.printStackTrace ();
	}
	catch (IOException e)
	{
	    e.printStackTrace ();
	}
    }


    //********************Stat plot functions (regression/curve fitting)********************
    //Regression methods follow the instructions given here:
    //http://www.zweigmedia.com/RealWorld/calctopic1/regression.html
    //http://www.had2know.com/academics/quadratic-regression-calculator.html
    //Converts arraylist objcts into doubles
    public static double xparse (int a)
    {
	String array = (String) x.get (a);
	xdoubleparse = Double.parseDouble (array);
	return xdoubleparse;

    }


    //Converts arraylist objcts into doubles
    public static double yparse (int a)
    {
	String array = (String) y.get (a);
	ydoubleparse = Double.parseDouble (array);
	return ydoubleparse;
    }


    //Sums all x values
    public static double xsum ()
    {
	double xsum = 0;
	xArray = new double [x.size ()];
	for (int i = 0 ; i < x.size () ; i++)
	{
	    xArray [i] = xparse (i);
	    xsum = xsum + xArray [i];
	}
	return xsum;

    }


    //Sums all y values
    public static double ysum ()
    {
	double ysum = 0;
	yArray = new double [y.size ()];
	for (int i = 0 ; i < y.size () ; i++)
	{
	    yArray [i] = yparse (i);
	    ysum = ysum + yArray [i];

	}
	return ysum;

    }


    //Sums the products of corresponding x and y values
    public static double xysum ()
    {
	double xysum = 0;
	xyArray = new double [x.size ()];
	for (int i = 0 ; i < x.size () ; i++)
	{
	    xyArray [i] = xparse (i) * yparse (i);
	    xysum = xysum + xyArray [i];

	}
	return xysum;
    }


    //Sums the squares of x values
    public static double xsqsum ()
    {
	double xsq = 0;
	xsqArray = new double [x.size ()];
	for (int i = 0 ; i < x.size () ; i++)
	{
	    xsqArray [i] = Math.pow (xparse (i), 2);
	    xsq = xsq += xsqArray [i];
	}
	return xsq;
    }


    //Sums the squares of y values
    public static double ysqsum ()
    {
	double ysq = 0;
	ysqArray = new double [y.size ()];
	for (int i = 0 ; i < y.size () ; i++)
	{
	    ysqArray [i] = Math.pow (yparse (i), 2);
	    ysq = ysq += ysqArray [i];

	}
	return ysq;

    }


    //Calculates x coefficient for linear regression equation
    public static double valuea ()
    {
	double vala = (((x.size () * xysum ()) - (xsum () * ysum ())) / ((x.size () * xsqsum ()) - (Math.pow (xsum (), 2))));
	return vala;
    }


    //Calculates additive constant for linear regression equation
    public static double valueb ()
    {
	double valb = ((ysum () * xsqsum ()) - (xsum () * xysum ())) / ((x.size () * xsqsum ()) - (Math.pow (xsum (), 2)));
	return valb;
    }


    //Calculates r value (a measure of correlation)
    public static double valuer ()
    {

	double r2 = ((x.size () * xysum ()) - (xsum () * ysum ())) / (Math.sqrt ((x.size () * xsqsum () - Math.pow (xsum (), 2)) * (x.size () * ysqsum () - Math.pow (ysum (), 2))));
	return r2;
    }


    //Calculates r square value (a measure of regression accuracy)
    public static double valuersq ()
    {
	double rsq = Math.pow (valuer (), 2);
	return rsq;
    }


    //Exponential regression methods
    //Converts all y values into their logarithms
    public static void ylogs ()
    {

	ylogArray = new double [y.size ()];
	for (int i = 0 ; i < y.size () ; i++)
	{
	    ylogArray [i] = Math.log (yparse (i));
	}
    }


    //Sets the new y values
    public static void newyval ()
    {
	for (int i = 0 ; i < x.size () ; i++)
	{
	    y.set (i, String.valueOf (ylogArray [i]));
	}
    }


    //Calculates exponential constants
    //Uses the linear regression methods, because of the log conversion
    //in the form of R= Ar^t - A = 10^ linear a -- r = 10^ linear b
    //Calculates multiplicative constant (i.e. A)
    public static double exponentialvaluea ()
    {
	double exvala = Math.exp (valuea ());
	return exvala;
    }


    //Calculates rate of change (i.e. r)
    public static double exponentialvaluerate ()
    {
	double exvalrate = Math.exp (valueb ());
	return exvalrate;
    }


    //Quadratic regression methods
    //Sums cubes of x values
    public static double xcubesum ()
    {
	double xcube = 0;
	xcubeArray = new double [x.size ()];
	for (int i = 0 ; i < x.size () ; i++)
	{
	    xcubeArray [i] = Math.pow (xparse (i), 3);
	    xcube = xcube += xcubeArray [i];
	}
	return xcube;
    }


    //Sums fourth powers of x values
    public static double xtesseractsum ()
    {
	double xtesseract = 0;
	xtesseractArray = new double [x.size ()];
	for (int i = 0 ; i < x.size () ; i++)
	{
	    xtesseractArray [i] = Math.pow (xparse (i), 4);
	    xtesseract = xtesseract += xtesseractArray [i];
	}
	return xtesseract;
    }


    //Sums numbers of the form x^2*y, x and y being a corresponding pair
    public static double xsqysum ()
    {
	double xsqy = 0;
	xsqyArray = new double [x.size ()];
	for (int i = 0 ; i < x.size () ; i++)
	{
	    xsqyArray [i] = Math.pow (xparse (i), 2) * yparse (i);
	    xsqy = xsqy += xsqyArray [i];
	}
	return xsqy;

    }


    //Gaussian elimination for each of the 6 matrices
    public static void matrix0 ()
    {
	matrix0 [0] [0] = xtesseractsum ();
	matrix0 [0] [1] = xcubesum ();
	matrix0 [0] [2] = xsqsum ();
	matrix0 [0] [3] = xsqysum ();

	matrix0 [1] [0] = xcubesum ();
	matrix0 [1] [1] = xsqsum ();
	matrix0 [1] [2] = xsum ();
	matrix0 [1] [3] = xysum ();

	matrix0 [2] [0] = xsqsum ();
	matrix0 [2] [1] = xsum ();
	matrix0 [2] [2] = x.size ();
	matrix0 [2] [3] = ysum ();
    }


    public static void matrix1 ()
    {
	matrix1 [0] [0] = matrix0 [0] [0] / matrix0 [0] [0];
	matrix1 [0] [1] = matrix0 [0] [1] / matrix0 [0] [0];
	matrix1 [0] [2] = matrix0 [0] [2] / matrix0 [0] [0];
	matrix1 [0] [3] = matrix0 [0] [3] / matrix0 [0] [0];

	matrix1 [1] [0] = matrix0 [1] [0];
	matrix1 [1] [1] = matrix0 [1] [1];
	matrix1 [1] [2] = matrix0 [1] [2];
	matrix1 [1] [3] = matrix0 [1] [3];

	matrix1 [2] [0] = matrix0 [2] [0];
	matrix1 [2] [1] = matrix0 [2] [1];
	matrix1 [2] [2] = matrix0 [2] [2];
	matrix1 [2] [3] = matrix0 [2] [3];
    }


    public static void matrix2 ()
    {
	matrix2 [0] [0] = matrix1 [0] [0];
	matrix2 [0] [1] = matrix1 [0] [1];
	matrix2 [0] [2] = matrix1 [0] [2];
	matrix2 [0] [3] = matrix1 [0] [3];

	matrix2 [1] [0] = matrix1 [1] [0] - matrix1 [0] [0] * matrix1 [1] [0];
	matrix2 [1] [1] = matrix1 [1] [1] - matrix1 [0] [1] * matrix1 [1] [0];
	matrix2 [1] [2] = matrix1 [1] [2] - matrix1 [0] [2] * matrix1 [1] [0];
	matrix2 [1] [3] = matrix1 [1] [3] - matrix1 [0] [3] * matrix1 [1] [0];

	matrix2 [2] [0] = matrix1 [2] [0] - matrix1 [0] [0] * matrix1 [2] [0];
	matrix2 [2] [1] = matrix1 [2] [1] - matrix1 [0] [1] * matrix1 [2] [0];
	matrix2 [2] [2] = matrix1 [2] [2] - matrix1 [0] [2] * matrix1 [2] [0];
	matrix2 [2] [3] = matrix1 [2] [3] - matrix1 [0] [3] * matrix1 [2] [0];
    }


    public static void matrix3 ()
    {
	matrix3 [0] [0] = matrix2 [0] [0];
	matrix3 [0] [1] = matrix2 [0] [1];
	matrix3 [0] [2] = matrix2 [0] [2];
	matrix3 [0] [3] = matrix2 [0] [3];

	matrix3 [1] [0] = matrix2 [1] [0] / matrix2 [1] [1];
	matrix3 [1] [1] = matrix2 [1] [1] / matrix2 [1] [1];
	matrix3 [1] [2] = matrix2 [1] [2] / matrix2 [1] [1];
	matrix3 [1] [3] = matrix2 [1] [3] / matrix2 [1] [1];

	matrix3 [2] [0] = matrix2 [2] [0];
	matrix3 [2] [1] = matrix2 [2] [1];
	matrix3 [2] [2] = matrix2 [2] [2];
	matrix3 [2] [3] = matrix2 [2] [3];
    }


    public static void matrix4 ()
    {
	matrix4 [0] [0] = matrix3 [0] [0] - matrix3 [1] [0] * matrix3 [0] [1];
	matrix4 [0] [1] = matrix3 [0] [1] - matrix3 [1] [1] * matrix3 [0] [1];
	matrix4 [0] [2] = matrix3 [0] [2] - matrix3 [1] [2] * matrix3 [0] [1];
	matrix4 [0] [3] = matrix3 [0] [3] - matrix3 [1] [3] * matrix3 [0] [1];

	matrix4 [1] [0] = matrix3 [1] [0];
	matrix4 [1] [1] = matrix3 [1] [1];
	matrix4 [1] [2] = matrix3 [1] [2];
	matrix4 [1] [3] = matrix3 [1] [3];

	matrix4 [2] [0] = matrix3 [2] [0] - matrix3 [1] [0] * matrix3 [2] [1];
	matrix4 [2] [1] = matrix3 [2] [1] - matrix3 [1] [1] * matrix3 [2] [1];
	matrix4 [2] [2] = matrix3 [2] [2] - matrix3 [1] [2] * matrix3 [2] [1];
	matrix4 [2] [3] = matrix3 [2] [3] - matrix3 [1] [3] * matrix3 [2] [1];
    }


    public static void matrix5 ()
    {
	matrix5 [0] [0] = matrix4 [0] [0];
	matrix5 [0] [1] = matrix4 [0] [1];
	matrix5 [0] [2] = matrix4 [0] [2];
	matrix5 [0] [3] = matrix4 [0] [3];

	matrix5 [1] [0] = matrix4 [1] [0];
	matrix5 [1] [1] = matrix4 [1] [1];
	matrix5 [1] [2] = matrix4 [1] [2];
	matrix5 [1] [3] = matrix4 [1] [3];

	matrix5 [2] [0] = matrix4 [2] [0] / matrix4 [2] [2];
	matrix5 [2] [1] = matrix4 [2] [1] / matrix4 [2] [2];
	matrix5 [2] [2] = matrix4 [2] [2] / matrix4 [2] [2];
	matrix5 [2] [3] = matrix4 [2] [3] / matrix4 [2] [2];

    }


    public static void matrix6 ()
    {
	matrix6 [0] [0] = matrix5 [0] [0] - matrix5 [2] [0] * matrix5 [0] [2];
	matrix6 [0] [1] = matrix5 [0] [1] - matrix5 [2] [1] * matrix5 [0] [2];
	matrix6 [0] [2] = matrix5 [0] [2] - matrix5 [2] [2] * matrix5 [0] [2];
	matrix6 [0] [3] = matrix5 [0] [3] - matrix5 [2] [3] * matrix5 [0] [2];

	matrix6 [1] [0] = matrix5 [1] [0] - matrix5 [2] [0] * matrix5 [1] [2];
	matrix6 [1] [1] = matrix5 [1] [1] - matrix5 [2] [1] * matrix5 [1] [2];
	matrix6 [1] [2] = matrix5 [1] [2] - matrix5 [2] [2] * matrix5 [1] [2];
	matrix6 [1] [3] = matrix5 [1] [3] - matrix5 [2] [3] * matrix5 [1] [2];

	matrix6 [2] [0] = matrix5 [2] [0];
	matrix6 [2] [1] = matrix5 [2] [1];
	matrix6 [2] [2] = matrix5 [2] [2];
	matrix6 [2] [3] = matrix5 [2] [3];
    }


    //Averages y values
    public static double ymean ()
    {
	double ymean;
	ymean = ysum () / y.size ();
	return ymean;
    }


    //Sum of squared totals
    public static double sse ()
    {
	double ssesum = 0;
	sseArray = new double [x.size ()];
	for (int i = 0 ; i < x.size () ; i++)
	{
	    sseArray [i] = Math.pow (yparse (i) - (matrix6 [0] [3] * Math.pow (xparse (i), 2)) - (matrix6 [1] [3] * xparse (i)) - matrix6 [2] [3], 2);
	    ssesum = ssesum += sseArray [i];
	}
	return ssesum;
    }


    //Sum of squared errors
    public static double sst ()
    {
	double sstsum = 0;
	sstArray = new double [x.size ()];
	for (int i = 0 ; i < x.size () ; i++)
	{
	    sstArray [i] = Math.pow (yparse (i) - ymean (), 2);
	    sstsum = sstsum += sstArray [i];
	}
	return sstsum;
    }


    //Returns r value - r has no intrinsic meaning here
    public static double quadr ()
    {
	double quadr;
	quadr = Math.sqrt (1 - (sse () / sst ()));
	return quadr;
    }


    //Returns r square value (a measure of regression accuracy)
    public static double quadrsq ()
    {
	double quadrsq;
	quadrsq = Math.pow (quadr (), 2);
	return quadrsq;
    }


    //Stat plot tab
    public static void statTab ()
    {
	//Infinite loop
	for (int k = 0 ;; k++)
	{
	    bg (3);
	    //Prompt
	    print ("Welcome to Stat Plot. Enter c for calculator, g for graphing, l for library, and any other key to continue.");
	    choice = c.getChar ();
	    lnCount = 0;
	    c.setCursor (1, 1);
	    box ();
	    //Exits the method if the user chooses to go to another tab
	    if (choice == 'l' || choice == 'c' || choice == 'g')
	    {
		return;
	    }
	    int count = 0;
	    x.clear ();
	    y.clear ();
	    //Prompts for x and y values to plot
	    for (int i = 0 ;; i++)
	    {
		box ();
		print ("Enter x value (enter q to quit).");
		String q = readString ();
		box ();
		//Exits loop when user decides to stop entering values
		if (q.equals ("q"))
		{
		    break;
		}
		else
		{
		    x.add (q);
		    print ("Enter y value.");
		    q = readString ();
		    y.add (q);
		    count++;
		}
	    }
	    box ();
	    axes ();
	    //Plots all the points
	    c.setColor (Color.black);
	    for (int i = 0 ; i < count ; i++)
	    {
		c.fillOval (xPixel (Double.parseDouble (x.get (i).toString ())) - 5, yPixel (Double.parseDouble (y.get (i).toString ())) - 5, 10, 10);
	    }
	    //Prompt for settings change
	    print ("Press s to change graph window, any other key to continue.");
	    choice = c.getChar ();
	    c.setCursor (1, 1);
	    lnCount = 0;
	    if (choice == 's')
	    {
		//Infinite loop
		for (int j = 0 ;; j++)
		{
		    //Allows the user to modify graph window settings
		    String display;
		    Image settings = loadImage ("settings.jpg");
		    c.drawImage (settings, 200, 200, null);
		    c.setColor (Color.black);
		    Font f = new Font ("Arial", Font.PLAIN, 16);
		    c.setFont (f);
		    //xmin
		    c.setCursor (1, 1);
		    c.println ("Enter lower bound for x: ");
		    xmin = c.readDouble ();
		    display = Double.toString (xmin);
		    c.drawString (display, 394, 291);
		    //ymin
		    c.setCursor (1, 1);
		    c.println ("Enter lower bound for y: ");
		    ymin = c.readDouble ();
		    display = Double.toString (ymin);
		    c.drawString (display, 394, 328);
		    //xmax
		    c.setCursor (1, 1);
		    xmax = checkRange (xmin, "Enter upper bound for x: ");
		    display = Double.toString (xmax);
		    c.drawString (display, 394, 365);
		    //ymax
		    c.setCursor (1, 1);
		    ymax = checkRange (ymin, "Enter upper bound for y: ");
		    display = Double.toString (ymax);
		    c.drawString (display, 394, 402);
		    //increment
		    increment = (xmax - xmin) / 2000;
		    display = Double.toString (increment);
		    c.drawString (display, 394, 439);
		    box ();
		    c.setCursor (1, 1);
		    lnCount = 0;
		    bg (3);
		    axes ();
		    //Re-plots the points according to the new settings
		    for (int i = 0 ; i < count ; i++)
		    {
			c.fillOval (xPixel (Double.parseDouble (x.get (i).toString ())) - 5, yPixel (Double.parseDouble (y.get (i).toString ())) - 5, 10, 10);
		    }
		    //Prompt again
		    print ("Press s to change graph window again, any other key to continue.");
		    choice = c.getChar ();
		    //Exits the loop if the user doesn't want to modify settings again
		    if (choice != 's')
		    {
			break;
		    }
		}
	    }
	    //Prompt for regression
	    print ("Press 1 for linear, 2 for exponential, 3 for quadratic regression.");
	    choice = c.getChar ();
	    c.setCursor (1, 1);
	    lnCount = 0;
	    //Linear regression
	    if (choice == '1')
	    {
		print ("Equation: y=" + valuea () + "x+" + valueb ());
		print ("r=" + valuer () + ", r^2=" + valuersq () + "\tPress any key to plot again.");
		//Graphs the regression equation
		graph (("0" + valuea () + "*x+0" + valueb ()).toCharArray ());
	    }
	    //Exponential regression
	    else if (choice == '2')
	    {
		ylogs ();
		newyval ();
		print ("Equation: y=" + exponentialvaluerate () + "*" + exponentialvaluea () + "^x");
		print ("Press any key to plot again.");
		//Graphs the regression equation
		graph (("0" + exponentialvaluerate () + "*(0" + exponentialvaluea () + ")^x").toCharArray ());
	    }
	    //Quadratic regression
	    else if (choice == '3')
	    {
		matrix0 ();
		matrix1 ();
		matrix2 ();
		matrix3 ();
		matrix4 ();
		matrix5 ();
		matrix6 ();
		print ("Equation: y=" + matrix6 [0] [3] + "*x^2+" + matrix6 [1] [3] + "*x+" + matrix6 [2] [3]);
		print ("r^2=" + quadrsq () + "\tPress any key to plot again.");
		//Graphs the regression equation
		graph (("0" + matrix6 [0] [3] + "*x^2+0" + matrix6 [1] [3] + "*x+0" + matrix6 [2] [3]).toCharArray ());
	    }
	    //Returns to the first stat plot screen (i.e. loop increments again)
	    c.getChar ();
	}
    }


    //Graphing calculator tab
    public static void graphTab ()
    {
	//Infinite loop
	for (int i = 0 ;; i++)
	{
	    //Resets error
	    error = false;
	    bg (1);
	    //Prompt
	    print ("Enter an equation (enter c for calculator, l for library, s for statplot): ");
	    String x = readString ();
	    //Exits the method if the user chooses to go to another tab
	    if (x.equals ("l") || x.equals ("c") || x.equals ("s"))
	    {
		choice = x.charAt (0);
		break;
	    }
	    //Converts equation into character array
	    else
	    {
		eq = x.toCharArray ();
	    }
	    bg (1);
	    //Graphs the equation
	    graph (eq);
	    //If the equation is invalid, the error message is displayed
	    if (error)
	    {
		Image error = loadImage ("error.jpg");
		c.drawImage (error, 200, 200, null);
		c.getChar ();
		//Increments the loop again, skipping the rest of the loop
		continue;
	    }
	    //Infinite loop
	    for (int k = 0 ;; k++)
	    {
		box ();
		c.setCursor (1, 1);
		//Prompt
		print ("Press m for menu, s to change settings, q to graph again.");
		choice = c.getChar ();
		//Displays menu
		if (choice == 'm')
		{
		    Image menu = loadImage ("menu.jpg");
		    c.drawImage (menu, 200, 200, null);
		    double a, b;
		    choice = c.getChar ();
		    bg (1);
		    graph (eq);
		    //Trace function
		    if (choice == '1')
		    {
			a = filter ("Enter x value: ");
			trace (a);
		    }
		    //Minimum function
		    else if (choice == '2')
		    {
			a = filter ("Enter lower bound: ");
			b = filter ("Enter upper bound: ");
			min (xValues, yValues, a, b);
		    }
		    //Maximum function
		    else if (choice == '3')
		    {
			a = filter ("Enter lower bound: ");
			b = filter ("Enter upper bound: ");
			max (xValues, yValues, a, b);
		    }
		    //Zeros function
		    else if (choice == '4')
		    {
			a = filter ("Enter lower bound: ");
			b = filter ("Enter upper bound: ");
			zeros (xValues, yValues, a, b);
		    }
		    //Derivative function
		    else if (choice == '5')
		    {
			a = filter ("Enter x value: ");
			derivative (xValues, yValues, a);
		    }
		    //Integral function
		    else if (choice == '6')
		    {
			a = filter ("Enter lower bound: ");
			b = filter ("Enter upper bound: ");
			integral (xValues, yValues, a, b);
			displayIntegral (xPixels, yPixels, a, b);
		    }
		    //Prompt to continue
		    print ("Press any key to continue.");
		    c.getChar ();
		}
		//Allows user to modify graph window settings
		else if (choice == 's')
		{
		    String display;
		    Image settings = loadImage ("settings.jpg");
		    c.drawImage (settings, 200, 200, null);
		    c.setColor (Color.black);
		    Font f = new Font ("Arial", Font.PLAIN, 16);
		    c.setFont (f);
		    //xmin
		    c.setCursor (1, 1);
		    lnCount = 0;
		    print ("Enter lower bound for x: ");
		    xmin = readDouble ();
		    display = Double.toString (xmin);
		    c.drawString (display, 394, 291);
		    //ymin
		    c.setCursor (1, 1);
		    lnCount = 0;
		    print ("Enter lower bound for y: ");
		    ymin = readDouble ();
		    display = Double.toString (ymin);
		    c.drawString (display, 394, 328);
		    //xmax
		    c.setCursor (1, 1);
		    lnCount = 0;
		    xmax = checkRange (xmin, "Enter upper bound for x: ");
		    display = Double.toString (xmax);
		    c.drawString (display, 394, 365);
		    //ymax
		    c.setCursor (1, 1);
		    lnCount = 0;
		    ymax = checkRange (ymin, "Enter upper bound for y: ");
		    display = Double.toString (ymax);
		    c.drawString (display, 394, 402);
		    //increment
		    increment = (xmax - xmin) / 2000;
		    display = Double.toString (increment);
		    c.drawString (display, 394, 439);
		    box ();
		    c.setCursor (1, 1);
		    bg (1);
		    //Re-graphs equation according to new settings
		    graph (eq);
		}
		//Quits the loop, allowing user to graph again
		else if (choice == 'q')
		{
		    break;
		}
	    }
	}
    }


    //Calculator tab
    public static void calcTab ()
    {
	String[] displayEq = {"", "", "", ""};
	String[] displayResult = {"", "", "", ""};
	bg (2);
	//Infinite loop
	for (int k = 0 ;; k++)
	{
	    //Erases erroneous results
	    if (error)
	    {
		bg (2);
		scrollDisplay (displayEq, displayResult);
	    }
	    error = false;
	    c.setCursor (1, 1);
	    box ();
	    //Prompt for equation
	    c.println ("Enter an equation (enter g for graphing, l for library, s for statplot): ");
	    String x = readString ();
	    //Exits the method if the user chooses to go to another tab
	    if (x.equals ("l") || x.equals ("g") || x.equals ("s"))
	    {
		choice = x.charAt (0);
		return;
	    }
	    //Calculation
	    String calcEq = x;
	    char[] calcEqChars = calcEq.toCharArray ();
	    String result = String.valueOf (calculate (calcEqChars, 0));
	    //If the equation is invalid, the error message is displayed
	    if (error)
	    {
		Image error = loadImage ("error.jpg");
		c.drawImage (error, 200, 200, null);
		c.getChar ();
		//Increments the loop again, skipping the rest of the loop
		continue;
	    }
	    //Loops through the arrays containing previous calculations
	    for (int i = 3 ; i > 0 ; i--)
	    {
		//Shifts all elements back by one
		displayEq [i] = displayEq [i - 1];
		displayResult [i] = displayResult [i - 1];
	    }
	    //Inserts newest inputs and outputs
	    displayEq [0] = calcEq;
	    displayResult [0] = result;
	    bg (2);
	    //Displays last four calculations using scrollDisplay method
	    scrollDisplay (displayEq, displayResult);
	    //Prints result
	    c.setCursor (1, 1);
	    c.println ("The result is " + result + ".");
	    //Prompt
	    c.println ("Press q to quit, press any other key to calculate again.");
	    choice = c.getChar ();
	    //Exits the method
	    if (choice == 'q')
	    {
		//Goes back to graphing calculator tab
		choice = 'g';
		return;
	    }
	}
    }


    //Library tab
    public static void libTab ()
    {
	//Infinite loop
	for (int i = 0 ;; i++)
	{
	    bg (4);
	    //Prompt
	    print ("Press 1 for help and syntax, 2 for functions library, 3 for credits.");
	    print ("Press g for graphing, c for calculator, s for stat plot.");
	    choice = c.getChar ();
	    int hPage = 1;
	    int fPage = 1;
	    bg (5);
	    //Exits the method if the user chooses to go to another tab
	    if (choice == 'g' || choice == 'c' || choice == 's')
	    {
		return;
	    }
	    //Help and syntax
	    else if (choice == '1')
	    {
		//Prints first page
		printFile ("syntax.txt");
		print ("Page 1 of 2.");
		choice = c.getChar ();
		if (choice == 'd')
		{
		    hPage++;
		}
		else
		{
		    //skips the rest of the loop and goes to the next iteration
		    continue;
		}
		//Infinite loop
		//At any point the user can go to the next page, previous page, or home (which breaks the loop)
		for (int k = 0 ;; k++)
		{
		    if (hPage == 1)
		    {
			bg (5);
			printFile ("syntax.txt");
			print ("Page 1 of 3.");
			choice = c.getChar ();
			if (choice == 'd')
			{
			    hPage++;
			}
			else
			{
			    break;
			}
		    }
		    else if (hPage == 2)
		    {
			bg (5);
			printFile ("disclaimers.txt");
			print ("Page 2 of 3.");
			choice = c.getChar ();
			if (choice == 'a')
			{
			    hPage--;
			}
			else if (choice == 'd')
			{
			    hPage++;
			}
			else
			{
			    break;
			}
		    }
		    else if (hPage == 3)
		    {
			bg (5);
			printFile ("navigation.txt");
			print ("Page 3 of 3.");
			choice = c.getChar ();
			if (choice == 'a')
			{
			    hPage--;
			}
			else
			{
			    break;
			}
		    }
		}
		continue;
	    }
	    //Functions library
	    else if (choice == '2')
	    {
		//Prints first page
		printFile ("functions.txt");
		print ("Page 1 of 3.");
		choice = c.getChar ();
		if (choice == 'd')
		{
		    fPage++;
		}
		else
		{
		    //skips the rest of the loop and goes to the next iteration
		    continue;
		}
		//Infinite loop
		//At any point the user can go to the next page, previous page, or home (which breaks the loop)
		for (int k = 0 ;; k++)
		{
		    if (fPage == 1)
		    {
			bg (5);
			printFile ("functions.txt");
			print ("Page 1 of 3.");
			choice = c.getChar ();
			if (choice == 'd')
			{
			    fPage++;
			}
			else
			{
			    break;
			}
		    }
		    else if (fPage == 2)
		    {
			bg (5);
			printFile ("graphtab.txt");
			print ("Page 2 of 3.");
			choice = c.getChar ();
			if (choice == 'a')
			{
			    fPage--;
			}
			else if (choice == 'd')
			{
			    fPage++;
			}
			else
			{
			    break;
			}
		    }
		    else if (fPage == 3)
		    {
			bg (5);
			printFile ("statplot.txt");
			print ("Page 3 of 3.");
			choice = c.getChar ();
			if (choice == 'a')
			{
			    fPage--;
			}
			else
			{
			    break;
			}
		    }
		}
		continue;
	    }
	    //Credits
	    else if (choice == '3')
	    {
		//Displays page
		c.setCursor (1, 1);
		c.println ("Press any key to go home.");
		printFile ("credits.txt");
		print ("Page 1 of 1.");
		c.getChar ();
	    }
	}
    }


    public static void main (String[] args)
    {
	c = new Console ();
	//Displays opening screen
	loadScreen ();
	//Sets everything to default values
	c.clear ();
	xmin = -10;
	xmax = 10;
	ymin = -6.67;
	ymax = 6.67;
	increment = 0.01;
	lnCount = 0;
	placeValue = 2;
	//Opening information
	bg (3);
	c.println ("Please read this very carefully. Press any key to continue.");
	printFile ("syntax.txt");
	c.getChar ();
	//Starts with graphing calculator tab
	graphTab ();
	//Infinite loop
	//Selects a specific tab based on the public char choice
	for (int i = 0 ;; i++)
	{
	    if (choice == 'g')
	    {
		graphTab ();
	    }
	    else if (choice == 'c')
	    {
		calcTab ();
	    }
	    else if (choice == 'l')
	    {
		libTab ();
	    }
	    else if (choice == 's')
	    {
		statTab ();
	    }
	}
    }
}


