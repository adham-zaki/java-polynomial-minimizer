import java.util.HashMap;
import java.util.TreeSet;

public class Minimizer {
	
	private double _eps;			// tolerance
	private int _maxIter;			// maximum number of iterations
	private double _stepSize;		// step size alpha
	private HashMap<String,Double> _var2x0;	   // starting point
	private HashMap<String,Double> _var2lastx; // last point found
	private double _lastObjVal;		// last obj fn value found
	private double _lastGradNorm;   // last gradient norm found
	private long _compTime;			// computation time needed
	private int _nIter;				// no. of iterations needed

	private HashMap<String,Polynomial> _var2gradp; // cached Polynomials for gradient expressions

	// Default constructor
	public Minimizer() {
		_eps = 0.001;
		_maxIter = 100;
		_stepSize = 0.05;
		_var2x0 = new HashMap<String,Double>();
		_var2lastx = new HashMap<String,Double>();
	}
	
	// Getters
	public double getEps()      { return _eps; }
	public int getMaxIter()     { return _maxIter; }
	public double getStepSize() { return _stepSize; }
	public HashMap<String,Double> getX0() { return _var2x0; }
	public double getLastObjVal()   { return _lastObjVal; }
	public double getLastGradNorm() { return _lastGradNorm; }
	public HashMap<String,Double> getLastPoint() { return _var2lastx; }
	public int getNIter()       { return _nIter; }
	public long getCompTime()   { return _compTime; }
	
	// Setters
	public void setEps(double e)      { _eps = e; }
	public void setMaxIter(int m)     { _maxIter = m; }
	public void setStepSize(double s) { _stepSize = s; }
	public void setX0(HashMap<String,Double> x0) { 
		// This has been done for you, but you should be able to produce 
		// this code on your own own.  Familiarize yourself with TreeSet 
		// operations (Google "Java TreeSet").
		_var2x0.clear(); 
		_var2x0.putAll(x0); // Copies over entries from x0 to _var2x0 
	}
	
	// Run the steepest descent algorithm (TODO, but a few details given)
	public void minimize(Polynomial p) throws Exception {
	
		// Check that starting point and polynomial have same free variables
		TreeSet<String> vars = p.getAllVars();
		if (!_var2x0.keySet().containsAll(vars)) {
			System.out.println("WARNING: Some variables in " + p + " are not assigned in " + _var2x0 + ".\n");
			_var2x0.clear();
			for (String v : vars)
				_var2x0.put(v, 0.0d);
		}
		_var2gradp= new HashMap<String, Polynomial>();
		for(String V : p.getAllVars())
			_var2gradp.put(V, p.differentiate(V));
		HashMap<String, Double> eval = new HashMap<String, Double>();	
		
		long t = System.currentTimeMillis();
		_var2lastx.putAll(_var2x0);
		_nIter=0;
		
		if(_var2gradp.isEmpty())
			throw new Exception();
	
		do{
			_nIter++;
			for(String key: _var2gradp.keySet()){
				double a = _var2gradp.get(key).evaluate(_var2lastx);
				eval.put(key, a);   //evaluate each derivative
			}
			_var2lastx = VectorUtils.sum(_var2lastx,VectorUtils.scalarMult(-getStepSize(),eval)); //step algorithm
			_lastObjVal = p.evaluate(_var2lastx); //find the value of the function at this new point
			_lastGradNorm = VectorUtils.computeL2Norm(eval);
			
			System.out.format("At iteration %d: %s objective value = %.3f\n", _nIter, VectorUtils.vectorToString(_var2lastx), _lastObjVal);
			
		}while(_nIter < _maxIter && _lastGradNorm > _eps);
		
		_compTime = System.currentTimeMillis()-t;
		// This is the output line for each iteration
		// System.out.format("At iteration %d: %s objective value = %.3f\n", _nIter, VectorUtils.vectorToString(_var2lastx), _lastObjVal);

		// TODO
	}

	public static void main(String[] args) throws Exception {
		
		// Assign starting point {x=1.0}
		HashMap<String,Double> x0 = new HashMap<String,Double>();
		x0.put("x", 1.0);

		// Initialize polynomial and minimizer
		Polynomial p = new Polynomial("10*x^2 + -40*x + 40");
		Minimizer  m = new Minimizer();		
		m.setX0(x0);

		System.out.println("Polynomial: " + p);

		// Run minimizer and view result at termination
		m.minimize(p);
		System.out.format("At termination: %s objective value = %.3f\n", m._var2lastx, m._lastObjVal);

		// Output of the test case above should read:
		// ===============================================
		// Polynomial: 10.000*x^2 + -40.000*x + 40.000
		// At iteration 1: {x=2.0} objective value = 0.000
		// At iteration 2: {x=2.0} objective value = 0.000
		// At termination: {x=2.0} objective value = 0.000
		// ===============================================
	}
}
