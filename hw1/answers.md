# Homework 1

Name: Max Bogue

### Key

- `k` is the # of threads (where s means sequential implementation)
- `t#` is the time of trial # in ms
- `t` is the best time in ms
- `ss` is the speedup vs sequential
- `sp` is the speedup vs parallel k=1
- `eff` is the efficiency vs parallel k=1

## Question 1

Arguments: `100 10000 7`

### Raw Times

    k   t1    t2    t3
    -------------------
    s    62    46    44
    1   106   101   102
    2   101    99   100
    3   100    99   102
    4   113   114   112
    5   114   116   118
    6   122   115   116
    7   125   125   132
    8   135   133   132

### Results

    k   t     ss      sp     eff
    -----------------------------
    s    44   1.000   2.30   2.30
    1   101   0.436   1.00   1.00
    2    99   0.444   1.02   0.51
    3    99   0.444   1.02   0.34
    4   112   0.393   0.90   0.23
    5   114   0.386   0.89   0.18
    6   115   0.383   0.88   0.15
    7   125   0.352   0.81   0.12
    8   132   0.333   0.76   0.10

## Question 2

Arguments: `large.txt`

### Raw Times

    k   t1    t2    t3
    -------------------
    1   144   142   154
    2   136   136   136
    3   121   121   120
    4   119   115   122
    5   114   111   112
    6   117   115   116
    7   114   116   163
    8   110   117   118

### Results

    k   t     sp     eff
    ---------------------
    1   142   1.00   1.00
    2   136   1.04   0.52
    3   120   1.18   0.39
    4   115   1.23   0.31
    5   111   1.28   0.26
    6   115   1.23   0.21
    7   114   1.25   0.18
    8   110   1.29   0.14

## Question 3

    n      k   t     speedup    eff     edsf
    ----------------------------------------
      40   1    2466    1.00    1.00    
      40   2    1516    1.63    0.81    0.23
      40   3    1215    2.03    0.68    0.24
      40   4    1052    2.34    0.59    0.24
     160   1    8049    1.00    1.00    
     160   2    4270    1.89    0.94    0.06
     160   3    3081    2.61    0.87    0.07
     160   4    2473    3.25    0.81    0.08
     360   1   17165    1.00    1.00    
     360   2    9011    1.90    0.95    0.05
     360   3    6280    2.73    0.91    0.05
     360   4    4896    3.51    0.88    0.05
     640   1   30473    1.00    1.00    
     640   2   15474    1.97    0.98    0.02
     640   3   10819    2.82    0.94    0.03
     640   4    8275    3.68    0.92    0.03
    1000   1   46864    1.00    1.00    
    1000   2   23826    1.97    0.98    0.02
    1000   3   16496    2.84    0.95    0.03
    1000   4   12634    3.71    0.93    0.03

    t       k    n        sizeup   sizeupeff
    ----------------------------------------
    15000   1     312.50    1.00        1.00
    15000   2     619.46    1.98        0.99
    15000   3     905.13    2.90        0.97
    15000   4    1195.40    3.83        0.96
    20000   1     419.65    1.00        1.00
    20000   2     835.09    1.99        0.99
    20000   3    1222.20    2.91        0.97
    20000   4    1608.34    3.83        0.96

## Question 4

### Raw Times

    k   t1     t2     t3
    ----------------------
    s   1194   1122   1105
    1   1153   1155   1158
    2    621    618    619
    3    440    561    440
    4    442    352    439
    5    371    297    372
    6    325    325    321
    7    246    294    293
    8    267    329    254

## Results

    k   t      ss     sp     eff
    -----------------------------
    s   1105   1.00   1.04   1.04
    1   1153   0.96   1.00   1.00
    2    618   1.79   1.87   0.93
    3    440   2.51   2.62   0.87
    4    352   3.14   3.28   0.82
    5    297   3.72   3.88   0.78
    6    321   3.44   3.59   0.60
    7    246   4.49   4.69   0.67
    8    254   4.35   4.54   0.57
