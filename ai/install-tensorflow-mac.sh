#!/bin/bash

# welcome message
echo ""
echo "Start. $(date)"
echo "This is simple script to help you to install TensorFlow on Mac."
echo "Note that this installlation uses only CPU rather than GPU, as I want my Macbook to live longer ;)"
echo ""

# installation parameters
# Mac OS X, CPU only, Python 2.7:
export TF_BINARY_URL=https://storage.googleapis.com/tensorflow/mac/cpu/tensorflow-0.11.0rc1-py2-none-any.whl

# install pip
curl -O https://bootstrap.pypa.io/get-pip.py
sudo python get-pip.py

# install TensorFlow
sudo pip install --ignore-installed --upgrade $TF_BINARY_URL

# test TensorFlow installation
testScript="test-tensorflow.py"
cat <<EOF > "$testScript"
import tensorflow as tf
hello = tf.constant('Hello, TensorFlow!')
sess = tf.Session()
print(sess.run(hello))
EOF

python "$testScript"

# success
echo ""
echo "Done. $(date)"
echo ""
