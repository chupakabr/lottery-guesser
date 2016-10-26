from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import tensorflow as tf
import numpy as np
import argparse

# TODO Rewrite using UK National Lottery data feed instead of this
# test from https://www.tensorflow.org/versions/r0.11/tutorials/mnist/beginners/index.html#mnist-for-ml-beginners

# Load test data
from tensorflow.examples.tutorials.mnist import input_data


def main(_):
    # Welcome message
    print("UK National Lottery Lotto predictor AI :)")
    print("")

    # Load data to learn
    mnist = input_data.read_data_sets("MNIST_DATA/", one_hot=True)

    # Create variable to be used in calculations
    DEFINITION_VALUES = 784
    NUMBERS_SUPPORTED = 10
    x = tf.placeholder(tf.float32, [None, DEFINITION_VALUES])
    W = tf.Variable(tf.zeros([DEFINITION_VALUES, NUMBERS_SUPPORTED]))
    b = tf.Variable(tf.zeros([NUMBERS_SUPPORTED]))

    # y = tf.nn.softmax(tf.matmul(x, W) + b)
    y = tf.matmul(x, W) + b

    # Training
    y_ = tf.placeholder(tf.float32, [None, NUMBERS_SUPPORTED])

    # The raw formulation of cross-entropy,
    #
    #   tf.reduce_mean(-tf.reduce_sum(y_ * tf.log(tf.softmax(y)),
    #                                 reduction_indices=[1]))
    #
    # can be numerically unstable.
    #
    # So here we use tf.nn.softmax_cross_entropy_with_logits on the raw
    # outputs of 'y', and then average across the batch.
    cross_entropy = tf.reduce_mean(
        tf.nn.softmax_cross_entropy_with_logits(y, y_))
    # cross_entropy = tf.reduce_mean(
    #    -tf.reduce_sum(y_ * tf.log(y), reduction_indices=[1]))

    train_step = tf.train.GradientDescentOptimizer(0.5).minimize(cross_entropy)
    init = tf.initialize_all_variables()

    with tf.Session() as sess:
        sess.run(init)
        for i in range(1000):
            batch_xs, batch_ys = mnist.train.next_batch(100)
            sess.run(train_step, feed_dict={x: batch_xs, y_: batch_ys})

        # Getting the results
        correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
        accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
        result = sess.run(
            accuracy, feed_dict={x: mnist.test.images, y_: mnist.test.labels})
        print("Result: " + str(result))

if __name__ == '__main__':
    print("")
    tf.app.run()
    print("")
