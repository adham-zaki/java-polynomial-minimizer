import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class Term {
	
	// For term "2.1*x^4*y*z^2" we would have the following member assignments...
	private double _coef; // = 2.1
	private ArrayList<String>  _vars; // = ["x", "y", "z"]
	private ArrayList<Integer> _pows; // = [4, 1, 2]

	// Constructor that only takes a coefficient
	public Term(double coef) {
		_coef = coef;
		_vars = new ArrayList<String>();
		_pows = new ArrayList<Integer>();
	}
	
	public Term(double coef, ArrayList<String> var, ArrayList<Integer> pow){
		_coef = coef;
		_vars = var;
		_pows = pow;
	}
	
	// Constructor that parses a String representation of a term
	public Term(String s) throws Exception {
		
		// Initialize this term
		_coef = 1.0d; // Will multiply any constants by this
		_vars = new ArrayList<String>();
		_pows = new ArrayList<Integer>();

		String[] factors = s.split("\\*");
		for (String factor : factors) {
			factor = factor.trim(); // Get rid of leading and trailing whitespace
			try {
				// If successful, multiplies in a constant (multiple constants in a product allowed)
				_coef *= Double.parseDouble(factor); 					
			} catch (NumberFormatException e) {
				// If not a coefficient, must be a factor "<var>^<pow>"
				// Must be a variable to a power -- parse the factor and add to list
				int pow = 1; // If no power, defaults to 1
				String[] var_pow = factor.split("\\^");
				String var = var_pow[0];
				if (var_pow.length == 2) {
					try { // Second part must be exponent
						pow = Integer.parseInt(var_pow[1]);
					} catch (NumberFormatException f) {
						throw new Exception("ERROR: could not parse " + factor);
					}
				} else if (var_pow.length > 2) 
					throw new Exception("ERROR: could not parse " + factor);
				
				// Successfully parsed variable and power, add to list
				if (_vars.contains(var))
					throw new Exception("ERROR: " + var + " appears twice in " + s);
				_vars.add(var);
				_pows.add(pow);
			}
		}
	}

	// Returns all variables in this term.  They are stored in an ArrayList<String>
	// abd one can construct a TreeSet<String> from an ArrayList<String> (and vice versa)
	// by passing one to the constructor of the other.  Familiarize yourself with
	// TreeSet operations (Google "Java TreeSet").
	public TreeSet<String> getAllVars() {
		return new TreeSet<String>(_vars);
	}
	
	// Returns a String representation of this term (can re-parse into same term)
	public String toString() {
		// Using "+" to append Strings involves a lot of String copies since Strings are 
		// immutable.  StringBuilder is much more efficient for append.
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%01.3f", _coef));
		for (int i = 0; i < _vars.size(); i++) {
			String var = _vars.get(i);
			int pow = _pows.get(i);
			sb.append("*" + var + (pow == 1 ? "" : "^" + pow));
		}
		return sb.toString();
	}
	
	// Sets the coefficient class member
	public void setCoef(double coef) {
		this._coef = coef;
	}

	// Returns the coefficient class member
	public double getCoef() {
		return _coef;
	}
	
	// Evaluate this term for the given variable assignments
	public double evaluate(HashMap<String, Double> assignments) throws Exception {
		double evaluated = _coef;
		boolean assigned;
		for (int i=0; i< _vars.size(); i++){
			assigned = false;
			for(String s: assignments.keySet()){
				if(_vars.get(i).equals(s)){
					evaluated *= Math.pow(assignments.get(s), _pows.get(i));
					assigned = true;
				}
			}
			if(assigned == false){
				throw new Exception("Variable not assigned");
			}
		}
	return evaluated;
	}

	// Provide the symbolic form resulting from differentiating this term w.r.t. var
	public Term differentiate(String var) {
		int i;
		ArrayList<String> vars = new ArrayList<String>();
		ArrayList<Integer> pows = new ArrayList<Integer>();
		double coef = _coef;
		for(i=0; i<_vars.size(); i++)
			vars.add(_vars.get(i));
		for(i=0; i<_pows.size(); i++)
			pows.add(_pows.get(i));
		for(i=0; i<vars.size(); i++){
			if(vars.get(i).equals(var)){
				if(pows.get(i)!= 1){
				coef = coef*pows.get(i);
				pows.set(i, pows.get(i)-1);
				return new Term(coef, vars, pows);
				}
				else{
				vars.remove(i);
				pows.remove(i);
				return new Term(coef, vars, pows);
				}
			}
		}
		return new Term(0.0d);
	}
}
	
