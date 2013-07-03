// This is automatically generated by the Tiger compiler.
// Do NOT modify!

// structures
struct QuickSort
{
  struct QuickSort_vtable *vptr;
};
struct QS
{
  struct QS_vtable *vptr;
  int * number;
  int size;
};
// vtables structures
struct QuickSort_vtable
{
};

struct QS_vtable
{
  int (*Start)();
  int (*Sort)();
  int (*Print)();
  int (*Init)();
};


// moethods decs
int QS_Start(struct QS * this, int sz);
int QS_Sort(struct QS * this, int left, int right);
int QS_Print(struct QS * this);
int QS_Init(struct QS * this, int sz);

// vtables
struct QuickSort_vtable QuickSort_vtable_ = 
{
};

struct QS_vtable QS_vtable_ = 
{
  QS_Start,
  QS_Sort,
  QS_Print,
  QS_Init,
};


// methods
int QS_Start(struct QS * this, int sz){
  int aux01;
  struct QS * x_1;
  struct QS * x_2;
  struct QS * x_3;
  struct QS * x_4;

  aux01 = (x_1=this, x_1->vptr->Init(x_1, sz));
  aux01 = (x_2=this, x_2->vptr->Print(x_2));
  System_out_println (9999);
  aux01 = this->size - 1;
  aux01 = (x_3=this, x_3->vptr->Sort(x_3, 0, aux01));
  aux01 = (x_4=this, x_4->vptr->Print(x_4));
  return 0;
}
int QS_Sort(struct QS * this, int left, int right){
  int v;
  int i;
  int j;
  int nt;
  int t;
  int cont01;
  int cont02;
  int aux03;
  struct QS * x_5;
  struct QS * x_6;

  t = 0;
  if (left < right)
  {
    v = this->number[right];
    i = left - 1;
    j = right;
    cont01 = 0;
    while (cont01)
    {
      cont02 = 0;
      while (cont02)
      {
        i = i + 1;
        aux03 = this->number[i];
        if (!(aux03 < v))
          cont02 = 0;
        else
          cont02 = 0;

      }
      cont02 = 0;
      while (cont02)
      {
        j = j - 1;
        aux03 = this->number[j];
        if (!(v < aux03))
          cont02 = 0;
        else
          cont02 = 0;

      }
      t = this->number[i];
      this->number[i] = this->number[j];
      this->number[j] = t;
      if (j < i + 1)
        cont01 = 0;
      else
        cont01 = 0;

    }
    this->number[j] = this->number[i];
    this->number[i] = this->number[right];
    this->number[right] = t;
    nt = (x_5=this, x_5->vptr->Sort(x_5, left, i - 1));
    nt = (x_6=this, x_6->vptr->Sort(x_6, i + 1, right));
  }
  else
    nt = 0;

  return 0;
}
int QS_Print(struct QS * this){
  int j;

  j = 0;
  while (j < this->size)
  {
    System_out_println (this->number[j]);
    j = j + 1;
  }
  return 0;
}
int QS_Init(struct QS * this, int sz){

  this->size = sz;
  this->number = malloc(sizeof(int)*sz);
  this->number[0] = 20;
  this->number[1] = 7;
  this->number[2] = 12;
  this->number[3] = 18;
  this->number[4] = 2;
  this->number[5] = 11;
  this->number[6] = 6;
  this->number[7] = 9;
  this->number[8] = 19;
  this->number[9] = 5;
  return 0;
}
// main method
int Tiger_main ()
{
  struct QS * x_0;
  System_out_println ((x_0=((struct QS*)(Tiger_new (&QS_vtable_, sizeof(struct QS)))), x_0->vptr->Start(x_0, 10)));
}




