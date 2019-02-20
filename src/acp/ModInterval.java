package acp;

public class ModInterval extends Real {
  static int[] primes = { 1865440751, 1504569917 };
  int[] m;
  DoubleInterval d;

  ModInterval (Real r) {
    m = new int[primes.length];
    if (r.lb() == r.ub())
      for (int i = 0; i < m.length; i++)
        m[i] = mod(r.lb(), primes[i]);
    else
      for (int i = 0; i < m.length; i++)
        m[i] = random.nextInt();
    d = new DoubleInterval(r);
  }    

  ModInterval (double x) {
    m = new int[primes.length];
    for (int i = 0; i < m.length; i++)
      m[i] = mod(x, primes[i]);
    d = new DoubleInterval(x);
  }

  private ModInterval (int[] m, Real r) {
    this.m = m;
    this.d = (DoubleInterval) r;
  }

  public int precision () { return 32; }

  public Real plus (Real r) {
    ModInterval that = (ModInterval) r;
    return new ModInterval(plus(this.m, that.m), this.d.plus(that.d));
  }

  public Real minus () {
    return new ModInterval(minus(m), d.minus());
  }

  public Real minus (Real r) {
    ModInterval that = (ModInterval) r;
    return new ModInterval(minus(this.m, that.m), this.d.minus(that.d));
  }

  public Real times (Real r) {
    ModInterval that = (ModInterval) r;
    return new ModInterval(times(this.m, that.m), this.d.times(that.d));
  }

  public Real over (Real r) {
    ModInterval that = (ModInterval) r;
    return new ModInterval(over(this.m, that.m), this.d.over(that.d));
  }

  public Real sqrt () {
    int[] m = new int[primes.length];
    for (int i = 0; i < m.length; i++)
      m[i] = random.nextInt();
    return new ModInterval(m, d.sqrt());
  }

  public int weakSign () { return d.weakSign(); }
  public double lb () { return d.lb(); }
  public double ub () { return d.ub(); }

  int sign () {
    int s = weakSign();
    if (s != 0)
      return s;
    System.out.println("m " + m[0] + " " + m[1]);
    System.out.println(d);
    for (int i = 1; i < m.length; i++)
      assert (m[i] == 0) == (m[0] == 0);
    if (m[0] == 0)
      return 0;
    throw new SignException();
  }

  static int inverse (int a, int p) {
    assert a != 0;
    long t = 0, nt = 1, r = p, nr = a;
    while (nr != 0) {
      long q = r/nr, pt = t, pr = r;
      t = nt;
      nt = pt - q * nt;
      r = nr;
      nr = pr - q * nr;
    }
    long i = t < 0 ? t + p : t;
    assert i * a % p == 1;
    return (int) i;
  }

  int mod (double x, int p) {
    long pow = 1;
    if (x > (1l<<60) || x < -(1l<<60)) {
      while (x != (long) x) {
        x /= 2;
        pow = 2 * pow % p;
      }
    }
    else {
      while (x != (long) x) {
        x *= 2;
        pow = (p + 1) / 2 * pow % p;
      }
    }
    int m = (int) ((long) x % p * pow % p);
    return m < 0 ? m + p : m;
  }

  static int[] plus (int[] a, int[] b) {
    int[] m = new int[primes.length];
    for (int i = 0; i < 2; i++)
      m[i] = (int) (((long) a[i] + (long) b[i]) % primes[i]);
    return m;
  }

  static int[] minus (int[] a) {
    int[] m = new int[primes.length];
    for (int i = 0; i < 2; i++)
      m[i] = primes[i] - a[i];
    return m;
  }

  static int[] minus (int[] a, int[] b) {
    int[] m = new int[primes.length];
    for (int i = 0; i < 2; i++)
      m[i] = (int) (((long) a[i] - (long) b[i]) % primes[i]);
    return m;
  }

  static int[] times (int[] a, int[] b) {
    int[] m = new int[primes.length];
    for (int i = 0; i < 2; i++)
      m[i] = (int) (((long) a[i] * (long) b[i]) % primes[i]);
    return m;
  }

  static int[] over (int[] a, int[] b) {
    int[] m = new int[primes.length];
    for (int i = 0; i < 2; i++)
      m[i] = (int) (((long) a[i] * (long) inverse(b[i], primes[i])) % primes[i]);
    return m;
  }
}
