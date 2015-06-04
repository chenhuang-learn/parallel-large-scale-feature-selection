#! /usr/bin/env python
import sys
import random
# sys.argv[1]: mushroom data set
# sys.argv[2]: train file
# sys.argv[3]: test file

all_lines = []
posi_num = 0
nega_num = 0
for line in open(sys.argv[1]):
    line = line.strip()
    fields = line.split(",")
    if fields[0] == "p":
        label = -1
        nega_num += 1
    else:
        label = 1
        posi_num += 1
    output_line = str(label) + " 0:1"
    for feature_index, feature_name in enumerate(fields[1:], start=1):
        if feature_index == 11:
            continue
        index = str(feature_index) + "-" + feature_name
        output_line += " " + index + ":1"
    output_line += "\n"
    all_lines.append(output_line)
print posi_num, nega_num

random.shuffle(all_lines)
train_end_index = int(len(all_lines) / 10 * 9)

train_handle = open(sys.argv[2], "w")
for i in range(train_end_index):
    train_handle.write(all_lines[i])
train_handle.close()
test_handle = open(sys.argv[3], "w")
for i in range(train_end_index, len(all_lines)):
    test_handle.write(all_lines[i])
test_handle.close()
