package de.sematre.mathparser;

import java.util.ArrayList;
import java.util.HashMap;

public class MathParser {

	private HashMap<String, MathFunction> functions = new HashMap<>();
	private char argumentSeparator = ';';
	private String equation = null;

	private Integer position = -1;
	private Integer ch = null;

	private Boolean parsingFunction = false;
	private Double[] args = null;

	public MathParser() {}

	public Double parse() {
		if (equation == null || equation.length() <= 0) {
			MathParsingExeption exeption = new MathParsingExeption("Equation is invalid!", "Equation is invalid!");
			exeption.setEquation(equation);
			throw exeption;
		}

		position = -1;
		ch = null;
		nextChar();

		Double x = parseExpression();
		if (position < equation.length()) throw createParsingExeption("Unexpected \"" + ((char) ch.intValue()) + "\" at " + (position + 1), equation, position);
		return x;
	}

	public Double parse(String equation) {
		setEquation(equation);
		return parse();
	}

	private Double parseFactor() {
		if (parseChar('+')) return parseFactor(); // unary plus
		if (parseChar('-')) return -parseFactor(); // unary minus

		Integer startPos = position;
		Double x = null;
		if (parseChar('(')) { // parentheses
			if (!parsingFunction) { // normal parentheses
				x = parseExpression();
				parseChar(')');
			} else { // function parentheses
				parsingFunction = false;

				ArrayList<Double> args = new ArrayList<>();
				args.add(parseExpression());

				while (parseChar(argumentSeparator)) {
					args.add(parseExpression());
				}

				this.args = new Double[args.size()];
				for (Integer i = 0; i < args.size(); i++) {
					this.args[i] = args.get(i);
				}

				parseChar(')');
				return null;
			}
		} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
			while ((ch >= '0' && ch <= '9') || ch == '.') {
				nextChar();
			}

			try {
				x = Double.parseDouble(equation.substring(startPos, position));
			} catch (NumberFormatException e) {
				throw createParsingExeption("Invalid number at " + startPos, equation, startPos, position - startPos == 1 ? startPos : position);
			}
		} else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) { // functions
			while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '.')
				nextChar();

			nextChar();
			if (parseChar(')')) {
				String func = equation.substring(startPos, position - 2);
				x = parseFunction(func, startPos, new Double[0]);
			} else {
				if (position > equation.length()) throw createParsingExeption("Missing chars: \"()\" at " + position, equation, position - 1, position);

				previousChar();
				String func = equation.substring(startPos, position);
				parsingFunction = true;
				parseFactor();

				try {
					x = parseFunction(func, startPos, args);
				} catch (ArrayIndexOutOfBoundsException e) {
					throw createParsingExeption("Missing argument for function \"" + func + "\" at " + position, equation, startPos, func.length() == 1 ? startPos : startPos + func.length());
				}
			}
		} else throw createParsingExeption("Unexpected \"" + ((char) ch.intValue()) + "\" at " + (position + 1), equation, startPos);

		if (parseChar('^')) x = Math.pow(x, parseFactor()); // exponentiation
		if (parseChar('?')) x %= parseFactor(); // modulo
		if (parseChar('%')) x /= 100D; // percent
		return x;
	}

	private Double parseExpression() {
		Double x = parseTerm();
		while (true) {
			if (parseChar('+')) x += parseTerm(); // addition
			else if (parseChar('-')) x -= parseTerm(); // subtraction
			else return x;
		}
	}

	private Double parseTerm() {
		Double x = parseFactor();
		while (true) {
			if (parseChar('*')) x *= parseFactor(); // multiplication
			else if (parseChar('/')) x /= parseFactor(); // division
			else return x;
		}
	}

	private Double parseFunction(String function, Integer startPos, Double[] args) {
		Double x = null;
		if (function.equalsIgnoreCase("sqrt")) x = Math.sqrt(args[0]);
		else if (function.equalsIgnoreCase("root")) x = Math.pow(args[1], (1D / args[0]));
		else if (function.equalsIgnoreCase("sin")) x = Math.sin(args[0]);
		else if (function.equalsIgnoreCase("cos")) x = Math.cos(args[0]);
		else if (function.equalsIgnoreCase("tan")) x = Math.tan(args[0]);
		else if (function.equalsIgnoreCase("toRadians")) x = Math.toRadians(args[0]);
		else if (function.equalsIgnoreCase("toDegrees")) x = Math.toDegrees(args[0]);
		else if (function.equalsIgnoreCase("log")) x = Math.log(args[0]);
		else if (function.equalsIgnoreCase("logx")) x = Math.log(args[1]) / Math.log(args[0]);
		else if (function.equalsIgnoreCase("log10")) x = Math.log10(args[0]);
		else if (function.equalsIgnoreCase("pi")) x = Math.PI;
		else if (function.equalsIgnoreCase("e")) x = Math.E;
		else if (function.equalsIgnoreCase("min")) x = Math.min(args[0], args[1]);
		else if (function.equalsIgnoreCase("max")) x = Math.max(args[0], args[1]);
		else if (function.equalsIgnoreCase("random")) x = Math.random();
		else {
			Double result = null;
			for (String currentFunction : functions.keySet()) {
				if (currentFunction.equalsIgnoreCase(function)) {
					result = functions.get(currentFunction).execute(args);
					if (result == null) throw createParsingExeption("Function \"" + currentFunction + "\" returns null at " + (startPos + 1), equation, startPos, startPos + function.length());
					break;
				}
			}

			if (result == null) throw createParsingExeption("Unknown function: \"" + function + "\" at " + (startPos + 1), equation, startPos, startPos + (function.length() > 1 ? function.length() : 0));
			x = result;
		}

		return x;
	}

	private void nextChar() {
		ch = (++position < equation.length()) ? equation.charAt(position) : -1;
	}

	private void previousChar() {
		ch = (--position >= 0) ? equation.charAt(position) : -1;
	}

	private Boolean parseChar(int regex) {
		while (ch == ' ')
			nextChar();
		if (ch == regex) {
			nextChar();
			return true;
		} else return false;
	}

	public String getEquation() {
		return equation;
	}

	public void setEquation(String equation) {
		this.equation = equation;
	}

	public char getArgumentSeparator() {
		return argumentSeparator;
	}

	public void setArgumentSeparator(char argumentSeparator) {
		this.argumentSeparator = argumentSeparator;
	}

	public void addFunction(String functionName, MathFunction function) {
		functions.put(functionName, function);
	}

	public void removeFunction(String functionName) {
		functions.remove(functionName);
	}

	public HashMap<String, MathFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(HashMap<String, MathFunction> functions) {
		this.functions = functions;
	}

	private static MathParsingExeption createParsingExeption(String message, String equation, Integer position) {
		return createParsingExeption(message, equation, position, position);
	}

	private static MathParsingExeption createParsingExeption(String message, String equation, Integer position, Integer target) {
		String errorReport = message + '\n' + equation + '\n';
		for (Integer i = 0; i < position; i++) {
			errorReport += ' ';
		}

		errorReport += '^';
		if (position != target) {
			for (Integer i = 1; (i + 1) < (target - position); i++) {
				errorReport += '~';
			}

			errorReport += '^';
		}

		MathParsingExeption exeption = new MathParsingExeption(message, errorReport);
		exeption.setEquation(equation);
		exeption.setStartPosition(position);
		exeption.setStopPosition(target);
		return exeption;
	}

	@FunctionalInterface
	public interface MathFunction {
		public Double execute(Double[] parameter);
	}

	public static class MathParsingExeption extends RuntimeException {

		private static final long serialVersionUID = 13688365781979795L;

		private String errorReport = null;

		private String equation = null;
		private Integer startPosition = null;
		private Integer stopPosition = null;

		public MathParsingExeption() {
			super();
		}

		public MathParsingExeption(String message) {
			super(message);
		}

		public MathParsingExeption(Throwable cause) {
			super(cause);
		}

		public MathParsingExeption(String message, Throwable cause) {
			super(message, cause);
		}

		public MathParsingExeption(String message, String errorReport) {
			super(message);
			this.errorReport = errorReport;
		}

		public String getErrorReport() {
			return errorReport;
		}

		public void setErrorReport(String errorReport) {
			this.errorReport = errorReport;
		}

		public String getEquation() {
			return equation;
		}

		public void setEquation(String equation) {
			this.equation = equation;
		}

		public Integer getStartPosition() {
			return startPosition;
		}

		public void setStartPosition(Integer startPosition) {
			this.startPosition = startPosition;
		}

		public Integer getStopPosition() {
			return stopPosition;
		}

		public void setStopPosition(Integer stopPosition) {
			this.stopPosition = stopPosition;
		}
	}
}