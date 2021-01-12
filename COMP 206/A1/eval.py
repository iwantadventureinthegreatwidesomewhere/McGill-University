'''
Evaluation of Assignment 0 (COMP 206)

Usage: python eval.py ./solutions

Here, solutions is the folder that contains the c file you have written.
If your source files are located elsewhere, change the argument
accordingly. There should be two files: q1_julia_explorer.c and q2_calendar.c
inside solutions.
'''


shape_template = '''
set terminal x11
set output

rmax = 5
nmax = 100

complex (x, y) = x * {1, 0} + y * {0, 1}
mandel (x, y, z, n) = (abs (z)> rmax || n>= 100)? n: mandel (x, y, z * z + complex (x, y), n + 1)

set xrange [-0.5:0.5]
set yrange [-0.5:0.5]
set logscale z
set samples 200
set isosample 200
set pm3d map
set size square
a= #A#
b= #B#
splot mandel(-a/100,-b/100,complex(x,y),0) notitle

'''

calendar_out = '''|-------------------------------------------------------|
|                        January                        |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
|       |       |       |       |       |       | 1     |
| 2     | 3     | 4     | 5     | 6     | 7     | 8     |
| 9     | 10    | 11    | 12    | 13    | 14    | 15    |
| 16    | 17    | 18    | 19    | 20    | 21    | 22    |
| 23    | 24    | 25    | 26    | 27    | 28    | 29    |
| 30    |       |       |       |       |       |       |
|-------------------------------------------------------|
|                       February                        |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
|       | 1     | 2     | 3     | 4     | 5     | 6     |
| 7     | 8     | 9     | 10    | 11    | 12    | 13    |
| 14    | 15    | 16    | 17    | 18    | 19    | 20    |
| 21    | 22    | 23    | 24    | 25    | 26    | 27    |
| 28    | 29    | 30    |       |       |       |       |
|-------------------------------------------------------|
|                         March                         |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
|       |       |       | 1     | 2     | 3     | 4     |
| 5     | 6     | 7     | 8     | 9     | 10    | 11    |
| 12    | 13    | 14    | 15    | 16    | 17    | 18    |
| 19    | 20    | 21    | 22    | 23    | 24    | 25    |
| 26    | 27    | 28    | 29    | 30    |       |       |
|-------------------------------------------------------|
|                         April                         |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
|       |       |       |       |       | 1     | 2     |
| 3     | 4     | 5     | 6     | 7     | 8     | 9     |
| 10    | 11    | 12    | 13    | 14    | 15    | 16    |
| 17    | 18    | 19    | 20    | 21    | 22    | 23    |
| 24    | 25    | 26    | 27    | 28    | 29    | 30    |
|-------------------------------------------------------|
|                          May                          |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
| 1     | 2     | 3     | 4     | 5     | 6     | 7     |
| 8     | 9     | 10    | 11    | 12    | 13    | 14    |
| 15    | 16    | 17    | 18    | 19    | 20    | 21    |
| 22    | 23    | 24    | 25    | 26    | 27    | 28    |
| 29    | 30    |       |       |       |       |       |
|-------------------------------------------------------|
|                         June                          |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
|       |       | 1     | 2     | 3     | 4     | 5     |
| 6     | 7     | 8     | 9     | 10    | 11    | 12    |
| 13    | 14    | 15    | 16    | 17    | 18    | 19    |
| 20    | 21    | 22    | 23    | 24    | 25    | 26    |
| 27    | 28    | 29    | 30    |       |       |       |
|-------------------------------------------------------|
|                         July                          |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
|       |       |       |       | 1     | 2     | 3     |
| 4     | 5     | 6     | 7     | 8     | 9     | 10    |
| 11    | 12    | 13    | 14    | 15    | 16    | 17    |
| 18    | 19    | 20    | 21    | 22    | 23    | 24    |
| 25    | 26    | 27    | 28    | 29    | 30    |       |
|-------------------------------------------------------|
|                        August                         |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
|       |       |       |       |       |       | 1     |
| 2     | 3     | 4     | 5     | 6     | 7     | 8     |
| 9     | 10    | 11    | 12    | 13    | 14    | 15    |
| 16    | 17    | 18    | 19    | 20    | 21    | 22    |
| 23    | 24    | 25    | 26    | 27    | 28    | 29    |
| 30    |       |       |       |       |       |       |
|-------------------------------------------------------|
|                       September                       |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
|       | 1     | 2     | 3     | 4     | 5     | 6     |
| 7     | 8     | 9     | 10    | 11    | 12    | 13    |
| 14    | 15    | 16    | 17    | 18    | 19    | 20    |
| 21    | 22    | 23    | 24    | 25    | 26    | 27    |
| 28    | 29    | 30    |       |       |       |       |
|-------------------------------------------------------|
|                        October                        |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
|       |       |       | 1     | 2     | 3     | 4     |
| 5     | 6     | 7     | 8     | 9     | 10    | 11    |
| 12    | 13    | 14    | 15    | 16    | 17    | 18    |
| 19    | 20    | 21    | 22    | 23    | 24    | 25    |
| 26    | 27    | 28    | 29    | 30    |       |       |
|-------------------------------------------------------|
|                       November                        |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
|       |       |       |       |       | 1     | 2     |
| 3     | 4     | 5     | 6     | 7     | 8     | 9     |
| 10    | 11    | 12    | 13    | 14    | 15    | 16    |
| 17    | 18    | 19    | 20    | 21    | 22    | 23    |
| 24    | 25    | 26    | 27    | 28    | 29    | 30    |
|-------------------------------------------------------|
|                       December                        |
|-------------------------------------------------------|
| Sunda | Monda | Tuesd | Wedne | Thurs | Frida | Satur |
|-------------------------------------------------------|
| 1     | 2     | 3     | 4     | 5     | 6     | 7     |
| 8     | 9     | 10    | 11    | 12    | 13    | 14    |
| 15    | 16    | 17    | 18    | 19    | 20    | 21    |
| 22    | 23    | 24    | 25    | 26    | 27    | 28    |
| 29    | 30    |       |       |       |       |       |
|-------------------------------------------------------|
'''

import os
import sys
import pexpect
import random

def run_test(exe, *args):
  print '-- Executing command --'
  print exe
  p = pexpect.spawn(exe)
  stdout = ''
  while True:
    try:
      stdout += p.read_nonblocking(10000, 1.)
    except (pexpect.EOF, pexpect.TIMEOUT) as e:
      break
  print '-- Your program\'s output --'
  print stdout
  print '-- End of output'
  p.close()
  return stdout

help_text = 'Usage: python eval.py path\nWhere path is a location on the filesystem that contains your source file(s)'

def print_grade(grade, total_grade):
  print '%d/%d' %(grade, total_grade)
  #pass

def run_gcc(as_dir, c_file, exe):
  exe_path = os.path.join(as_dir, exe)
  gcc = 'gcc %s -o %s' % (os.path.join(as_dir, c_file), exe_path)
  print 'Running', gcc
  os.system(gcc)
  if not os.path.exists(exe_path):
    print '******** Compilation failed. ************\n'
    return False, None
  print '-- Compilation Done\n'
  return True, exe_path

# Q1 Related ==============================================================================
def compare_output_special(stdout, correct):
  stdout_lines = stdout.split('\n')
  correct_lines = correct.split('\n')
  if not len(stdout_lines) == len(correct_lines):
    print 'The two output do not have the same number of lines'
    return False
  for i, std_line in enumerate(stdout_lines):
    matched = True
    if std_line.startswith('a=') or std_line.startswith('b='):
      if not std_line.strip()[:-3] == correct_lines[i].strip()[:-3]: # This is hacky, hopefully it works fine
        matched = False
      
    elif not std_line.strip() == correct_lines[i].strip():
      matched = False
    
    if not matched:
      print 'Line', (i+1), 'do not match:'
      print 'Yours  :', std_line
      print 'Correct:', correct_lines[i]
      return False
  return True
      

def evaluate_q1(as_dir, files):
  c_file = 'q1_julia_explorer.c'
  total = 40
  print '========= Evaluating Q1: Julia Explorer ============='
  if not c_file in files:
    print 'Did not find file called %s in %s. You must name your c file as %s' % (c_file, as_dir, c_file)
    print "Unable to automatically grade Q1"
    #print '\n-- Grade for Q1'
    #print_grade(0, total)
  
  status, exe_path = run_gcc(as_dir, c_file, 'q1_julia_explorer')
  if not status:
    print "Unable to automatically grade Q1"
    #print '\n-- Grade for Q1'
    #print_grade(0, total)
  
  print '-- CASE 1.1: This is a stochastic test case, where we randomly modify the shape template file each time you run this test. Your code should be able to cope with a slightly perturbed template file. Note that when replacing the tags, you must format the numbers as floating point numbers with exactly 6 digits after the decimal. If I run ./q1_julia_explorer shape_template.txt 35 65.123123123, then #A# should become 35.000000 and #B# should become 65.123123.'
  
  prepend_new_lines = ''.join(['\n'] * random.randint(1, 4))
  prepend_chars = 'garbage = 2.918' + ''.join(['8'] * random.randint(1,12)) + '\n'
  perturbed = prepend_chars + prepend_new_lines + shape_template
  perturbed_template = os.path.join(as_dir, 'perturbed_shape_temp.txt')
  with open(perturbed_template, 'w') as fh:
    fh.write(perturbed)
  
  
  stdout = run_test(exe_path + ' ' + perturbed_template + ' 35 65.123456789')
  correct_answer = perturbed.replace('#A#', '35.000000').replace('#B#', '65.123456')
  print '-- Expected output --'
  print correct_answer
  print '-- End of output'
  

  correct = compare_output_special(stdout, correct_answer)

  print 'Automarking Q1 complete. Your program\'s output is the same as expected output:', correct    
  
# Q2 Related ==============================================================================
def compare_output(stdout, correct):
  stdout_lines = stdout.split('\n')
  correct_lines = correct.split('\n')
  if not len(stdout_lines) == len(correct_lines):
    print 'The two output do not have the same number of lines'
    return False
  for i, std_line in enumerate(stdout_lines):
    matched = True
    if not std_line.strip() == correct_lines[i].strip():
      matched = False
    if not matched:
      print 'Line', (i+1), 'do not match:'
      print 'Yours  :', std_line
      print 'Correct:', correct_lines[i]
      return False
  return True
  
def evaluate_q2(as_dir, files):
  c_file = 'q2_calendar.c'
  total = 60
  print '========= Evaluating Q2: Calendar ============='
  if not c_file in files:
    print 'Did not find file called %s in %s. You must name your c file as %s' % (c_file, as_dir, c_file)
    print 'Unable to automark Q2'
 
  status, exe_path = run_gcc(as_dir, c_file, 'q2_calendar')
  if not status:
    print 'Unable to automark Q2'
 
  print '-- CASE 2.1: Test calendar generation.'
  stdout = run_test(exe_path + ' 5 7')
  
  print '-- Expected output --'
  print calendar_out
  print '-- End of output'
  
  correct = compare_output(stdout, calendar_out)

  print 'Automarking complete for Q2. Your program\'s output is the same as expected output:', correct    

def evaluate():
  if len(sys.argv) < 2:
    print help_text
    return
  
  as_dir = sys.argv[1]
  files = os.listdir(as_dir)
  
  evaluate_q1(as_dir, files)
  evaluate_q2(as_dir, files)
  
if __name__ == '__main__':
  evaluate()




