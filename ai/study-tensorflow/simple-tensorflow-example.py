import tensorflow as tf
import numpy as np

print "Simple TensorFlow example }:->"
print ""

matrix1 = tf.constant([[3., 3.]])
matrix2 = tf.constant([[2.], [2.]])
matrixProduct = tf.matmul(matrix1, matrix2)

# y = x^2 + 2x + 1
x = tf.placeholder(tf.float32, shape=[1])
formulaProduct = tf.Variable(x*x + x*2 + 1)

with tf.Session() as sess:
    # Task1: matrix multiplication
    matrixResult = sess.run(matrixProduct)
    print("Matrix product: " + str(matrixResult))

    # Task2: formula evaluation
    for i in range(5):
        sess.run(tf.initialize_all_variables(), feed_dict={x: [i]})
        formulaResult = sess.run(formulaProduct)
        print("y=x^2+2x+1, for x=" + str(i) + ": " + str(formulaResult))

print ""
