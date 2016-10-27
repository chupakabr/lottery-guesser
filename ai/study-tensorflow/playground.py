import matplotlib.image as mpimg
import matplotlib.pyplot as plt
import tensorflow as tf

filename = "res/MarshOrchid.jpg"
image = mpimg.imread(filename)
print("Shape: " + str(image.shape))
# print("On load:")
# print(image)

# rawimage = tf.Variable(image)
# model = tf.initialize_all_variables()
rawimage = tf.placeholder(tf.uint8, [None, None, None])
sliceOp = tf.slice(rawimage, [500, 0, 0], [3000, -1, -1])

with tf.Session() as sess:
    # sess.run(model)
    # result = sess.run(tf.transpose(rawimage, perm=[1, 0, 2]))
    result = sess.run(sliceOp, feed_dict={rawimage: image})

# print("After tranposition to [1,0,2]:")

print("Share res: " + str(result.shape))
plt.imshow(result)
plt.show()
