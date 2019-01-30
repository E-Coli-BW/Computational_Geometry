package segment;
import acp.*;
import pv.*;

public class ABintersectCD extends GO<PV2> {
  public final GO<PV2> a, b, c, d;

  public ABintersectCD (GO<PV2> a, GO<PV2> b, GO<PV2>c, GO<PV2> d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

fv       }
  public PV2 calculate () {  c
  public PV2 calculate () {  c
  public PV2 calculate () {  c
  public PV2 calculate () {  c
  public PV2 calculate () {  c
  public PV2 calculate () {  c
  public PV2 calculate () {  c
  public PV2 calculate () {  c
  public PV2 calculate () {  c
  public PV2 calculate () {  c
  public PV2 calculate () {  c
  public PV2 calculate () {  c cv c  cv cv cv  c cv  cv cv cv cv cv cv
    PV2 pa = a.xyz(); //a
    PV2 pb = b.xyz(); //b
    PV2 pc = c.xyz(); //c
    PV2 pd = d.xyz(); //d
    PV2 vab = pb.minus(pa); //b-a
    PV2 vcd = pd.minus(pc); //d-c
    PV2 vdc = pc.minus(pd); //c-d
    PV2 vca = pa.minus(pc); //a-c
    PV2 vac = pc.minus(pa); //c-a

    // EXERCISE 2
    // Calculate point of intersection of ab and cd.
    //p=c-(d-c)*(c-a)*(b-a)/((d-c)*(b-a))
    //check if (d-c)x(b-a)==0 or not
    //Cross returns a value of type Real
    if(vcd.cross(vab).approx()!=0.0){
    PV2 complex_express = vcd.times(vac.cross(vab)).over(vcd.cross(vab));
    PV2 p = pc.minus(complex_express); // This should be correct
    }
    System.out.println("ap x ab " + p.minus(pa).cross(vab) + 
                        " cp x cd " + p.minus(pc).cross(vcd));
    return p;
  }
}
