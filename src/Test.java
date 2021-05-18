
abstract class Bool { // Базов клас за двуместна логическа функция
	private String name;
	private char abbr;

	public Bool(String n, char ab) {
		name = n;
		abbr = ab;
	}

	public String getName() {
		return name;
	}

	public char getAbbr() {
		return abbr;
	}

	// За удобство: превръщане на цяло число в логически тип
	public static boolean intToBoolean(int a) {
		return (a != 0) ? true : false;
	}

	// Абстрактна функция за резулата от изпълнението
	abstract public boolean exec(boolean a, boolean b);

	// Същата функция с параметри цели числа
	public boolean exec(int a, int b) {
		return exec(intToBoolean(a), intToBoolean(b));
	}
}

// Разпознаваеми двуменстни логически функции
class And extends Bool { // Конюнкция
	public And() {
		super("and", '&');
	}

	public boolean exec(boolean a, boolean b) {
		return a && b;
	}
}

class Or extends Bool {// Дизюнкция
	public Or() {
		super("or", '|');
	}

	public boolean exec(boolean a, boolean b) {
		return a || b;
	}
}

class Xor extends Bool {// Алтернатива
	public Xor() {
		super("xor", '^');
	}

	public boolean exec(boolean a, boolean b) {
		return a ^ b;
	}
}

class Equ extends Bool {// Еквивалентност
	public Equ() {
		super("equ", '=');
	}

	public boolean exec(boolean a, boolean b) {
		return a == b;
	}
}

class Imp extends Bool {// Импликация
	public Imp() {
		super("imp", '>');
	}

	public boolean exec(boolean a, boolean b) {
		return !(a && !b);
	}
}

class RImp extends Bool {// Обратна импликация
	public RImp() {
		super("revimp", '>');
	}

	public boolean exec(boolean a, boolean b) {
		return !(!a && b);
	}
}

class Nor extends Bool {// Стрелка на Пирс
	public Nor() {
		super("Pierce", '\\');
	}

	public boolean exec(boolean a, boolean b) {
		return !(a || b);
	}
}

class Nand extends Bool {// Черта на Шефър
	public Nand() {
		super("Sheffer", '/');
	}

	public boolean exec(boolean a, boolean b) {
		return !(a && b);
	}
}

class CalcNode {// Връх на дърво за изчисляване
	private char val;// Символ във върха
	private CalcNode left, right; // Ляво и дясно поддърво
	// Конструктор на листо

	public CalcNode(char v) {
		val = v;
		left = right = null;
	}

	// Конструктор по символ и два указателя
	public CalcNode(char v, CalcNode lft, CalcNode rgt) {
		val = v;
		left = lft;
		right = rgt;
	}

	public char getVal() {
		return val;
	}

	public CalcNode getLeft() {
		return left;
	}

	public CalcNode getRight() {
		return right;
	}

	@Override
	public String toString() {
		return (left == null ? "null" : "" + left.val) + " - " + val + " - "
				+ (right == null ? "null" : "" + right.val);
	}
}

class CalcTree { // Дърво за изчисляване на логически израз
	// Позволени символи
	private static final String op = "!&|^=><\\/()abcdefghijklmnopqrstuvwxyz";
	private CalcNode root; // Корен на дървото
	// Масив от логически функции
	private Bool[] f;
	private String source; // Низ, от който е произведено дървотою

	private void init() {// Инициализация на масива от функции
		f = new Bool[8];// Удобно е редът да съвпада с този в op
		f[0] = new And();
		f[1] = new Or();
		f[2] = new Xor();
		f[3] = new Equ();
		f[4] = new Imp();
		f[5] = new RImp();
		f[6] = new Nor();
		f[7] = new Nand();
	}

	// Приоритет на символ
	private int prior(char c) {
		int p = op.indexOf(c);
		if (p < 0)
			return -1; // Непозволен символ
		switch (p) {
		case 0:
			return 5;// !
		case 1:
			return 4;// &
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			return 3; // Неспециална операция
		case 9:
			return 2;// (
		case 10:
			return 1;// )
		}
		return 0;// буква [a..z]
	}

	// Място на последната операция в изчислителния процес
	// Връща -1, ако няма такова (всичко в скоби или само име на променлива)
	// Бръща -2, ако установи грешка
	private int cut(int start, int len) {
		int c = 0;// Брояч на скобите
		int pr = 7;// Приоритет на текущото място за разделяне (число, по-голямо от максимално
					// възможното)
		int p = -1;// Текущо място за разделяне на низа;
					// (остава -1, ако изразът е в скоби или е само променлива)
		for (int i = start; i < start + len; i++) {
			// System.out.println("^i="+source.charAt(i)+": "+op.indexOf(source.charAt(i)));
			switch (prior(source.charAt(i))) {
			case -1:
				return -2;// лош символ
			case 0:
				break;// променлива
			case 1: {
				c--;// затваряща скоба
				if (c < 0)
					return -2; // лоши скоби
				break;
			}
			case 2: {
				c++;
				break;
			} // отваряща скоба
			case 3: {// някоя от неспециалните операции
				if ((c == 0) // Ако е извън скоби
						&& pr >= 3) // и текущият приоритет е по-голям или равен на 3,
				{
					pr = 3; // текущият приоритет (о)става 3
					p = i; // и запомняме тази позиция за разделяне
				}
				break;
			}
			case 4: {// and
				if ((c == 0) // Ако е извън скоби
						&& pr >= 4) // и текущият приоритет е по-голям или равен на 4
				{
					pr = 4; // текущият приоритет (о)става 4
					p = i; // и запомняме тази позиция за разделяне
				}
				break;
			}
			case 5: {// Not.
						// ВНИМАНИЕ!
						// 1. Преди ! не може да има буква или затваряща скоба
				if (i > 0) {
					int pp = prior(source.charAt(i - 1));
					if (pp < 2)
						return -2;
				}
				// 2. Not се изпълнява от дясно наляво!
				if (c == 0 // Ако е извън скоби
						&& pr > 5) // строго!
				{
					pr = 5;
					p = i;
				}
			}
			}
			// System.out.println("c="+c+", p="+p+", pr="+pr);
		}
		// System.out.println("c="+c+", p="+p);
		return (c != 0) ? -2 : p; // Ако c не е нула - грешка: много отварящи скоби
	}

	// Празен конструктор
	public CalcTree() {
		root = null;
		source = "";
		init();
	}

	// Рекурсивен метод за изграждане на дървото по низа source
	// Връща null, ако възникне грешка, или указател към корена,
	// ако процесът завърши успешно
	private CalcNode makeTree(int start, int len) {
		// System.out.println("start="+start+", len="+len);
		if (len < 1)
			return null;// Празен низ
		int p = cut(start, len); // Място на разделяне на ляво и дясно поддърво
		// System.out.println("p="+p);
		if (p == -2)
			return null;// Грешка при намирането на място
		if (p == -1) { // Липса на място на прекъсване
			if (len == 1) { // Ако е един символ
				// Ако е малка латинска буква - листо
				if (prior(source.charAt(start)) == 0)
					return new CalcNode(source.charAt(start));
				// Иначе - грешка
				return null;
			}
			// Ако са повече от един символ, допустимо е само, когато всичко е заградено в
			// скоби
			// Ако не е така - грешка
			if (source.charAt(start) != '(' || source.charAt(start + len - 1) != ')')
				return null;
			// Иначе - резултатът е това, което е вътре в скобите
			return makeTree(start + 1, len - 2);
		}
		// Има място на прекъсване p
		int pr = prior(source.charAt(p));
		// Дясно поддърво
		CalcNode R = makeTree(p + 1, len - (p - start + 1));
		if (R == null)
			return null; // Ако възникне грешка - неуспешен край на изграждането
		// Ако е Not, няма ляво поддърво
		if (pr == 5)
			return new CalcNode('!', null, R);
		// Ляво поддърво
		CalcNode L = makeTree(start, p - start);
		if (L == null)
			return null;// Ако възникне грешка - неуспешен край на изграждането
		// Връх с ляво и дясно поддърво
		return new CalcNode(source.charAt(p), L, R);
	}

	// Конструктор по низ
	public CalcTree(String s) {
		source = s;
		init();
		root = makeTree(0, s.length());// Остава null, ако възникне грешка
	}

	public CalcNode getRoot() {
		return root;
	}

	public String getSource() {
		return source;
	}

	public boolean isValid() {
		return root != null;
	}

	// Метод за получаване на кодираната във v логическа стойност на променлива с
	// име в c
	private boolean getVal(char c, int v) {
		c -= 'a';
		return (v & (1 << c)) != 0;
	}

	// Рекурсивен метод за изчисляване на дървото
	private boolean calc(CalcNode n, int v) {
		if (n.getRight() == null)
			return getVal(n.getVal(), v);// листо
		if (n.getLeft() == null)
			return !calc(n.getRight(), v);// !
		int p = op.indexOf(n.getVal()) - 1; // Номер на функцията от op
		return f[p].exec(calc(n.getLeft(), v), calc(n.getRight(), v));
	}

	// Изчисляване на дървото
	public boolean calc(int values) throws IllegalStateException {
		if (root == null)
			throw new IllegalStateException();
		return calc(root, values);
	}

	// Рекурсивен метод за височината
	private int height(CalcNode n) {
		if (n == null)
			return 0;
		int p = height(n.getLeft());
		int q = height(n.getRight());
		return 1 + (p > q ? p : q);
	}

	public int getHeight() {// Височина на дървото
		return height(root);
	}

	// Рекурсивен метод за създаване на образ на дървото върху
	// канава (двумерен масив от символен тип)
	private void createArr(CalcNode n, int dist, int x, int y, char[][] buf) {
		if (n == null)
			return;
		buf[y][x] = n.getVal();
		if (n.getRight() != null) {
			buf[y + 1][x + dist] = '\\';
			createArr(n.getRight(), dist >> 1, x + dist, y + 2, buf);
		}
		if (n.getLeft() != null) {
			buf[y + 1][x - dist] = '/';
			createArr(n.getLeft(), dist >> 1, x - dist, y + 2, buf);
		}
	}

	@Override
	public String toString() {// Низ за изпращане на образ на дървото към поток
		if (root == null)
			return "Incorrect";
		int h = 2 * getHeight() - 1;// За всяко ниво, освен последното - два реда
		int w = (1 << getHeight()) + 1;// Необходима широчина на канавата
		char[][] b = new char[h][w]; // Инициализация на масива и запълване с интервали
		for (int i = 0; i < h; i++)
			for (int j = 0; j < w; j++)
				b[i][j] = ' ';
		createArr(root, w >> 2, w >> 1, 0, b);// Създаване на образа
		String s = "";// Превръщане в низ
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++)
				s += b[i][j];
			s += '\n'; // Нов ред в края на всеки ред
		}
		return s;
	}

	// Метод за преброяване на битовете със стойност 1
	// в целочислен тип
	private int count1(int a) {
		int c = 0;
		while (a != 0) {
			c++;
			a = a & (a - 1);
		}
		return c;
	}

	// Метод, връщащ броя на променливите в source
	public int varCount() {
		if (root == null)
			return 0;
		int a = 0;// по един бит за всяка буква (a-z)
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (prior(c) == 0) {// Ако е буква
				c -= 'a'; // Номер на бита, отговарящ за буквата
				a |= (1 << c);// (1<<c) е маска с всички битове 0, освен този на място c
								// OR с тази маска не променя другите битове, а този на мястп c
								// установява в 1 (не го променя, ако вече си е бил 1).
			}
		}
		return count1(a);
	}
}

public class Test {
	// Метод за проверка на тъждество
	public static void check(String left, String right) {
		// Създаване на израз, оразяващ логическа еквивалентност
		// на лявата и дясната част на тъждеството. Ако тъждеството
		// е вярно, той трябва винати да се изчислява като true.
		String s = "(" + left + ")=(" + right + ")";
		CalcTree t = new CalcTree(s);// Дърво за изчисляване върху s
		if (!t.isValid()) {
			System.out.println("Error");
			return;
		}
		try {
			// Проверка при всевъзможните набори от стойности
			for (int v = 0; v < (1 << t.varCount()); v++) {
				if (!t.calc(v)) {// Намерен контрапример
					char c = 'a';// Име на променлива
					// Извеждане на контрапримера
					for (int i = 0; i < t.varCount(); i++) {
						System.out.print("" + c + "=" + (v & 1) + ' ');
						c++;// Име на следащата променлива
						v >>>= 1;// Премахване на най-младшия бит, измествайки всички надясно.
					}
					System.out.println();// Нов ред и изход
					return;
				}
			}
			// Няма контрапример.
			System.out.println("TRUE");
		} catch (Exception e) {
			System.out.println("Illegal tree");
		}
	}

	public static void main(String[] args) {
		check("a&b", "a\\a\\(b\\b)");
	}
}
