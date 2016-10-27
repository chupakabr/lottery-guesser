from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import tensorflow as tf
import numpy as np
import argparse
import csv
import requests

# Lotto results in CSV
CSV_FEED = 'https://www.national-lottery.co.uk/results/lotto/draw-history/csv'


def main(_):
    # Welcome message
    print("UK National Lottery Lotto predictor AI :)")
    print("")

    # Load data to learn
    with requests.Session() as sess:
        request = sess.get(CSV_FEED)
        content = request.content.decode('utf-8')

        dataDesc = []
        csvData = csv.reader(content.splitlines(), delimiter=',')
        for line in csvData:
            dataDesc.append(
                [line[1], line[2], line[3], line[4], line[5], line[6]])

        # CSV header to be removed
        dataDesc.pop(0)
        print(dataDesc)

        # Build draw history array
        drawHistory = np.asarray(dataDesc)

    print("Draws history shape: " + str(drawHistory.shape))

    print("TODO")

if __name__ == '__main__':
    print("")
    tf.app.run()
    print("")
