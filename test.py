import re
import sys


def parse_line(line):
    time = '[0-9]{2}\\:[0-9]{2}\\:[0-9]{2}\\.[0-9]{3}'
    phil = '\\[(Hungry\\-)?Philosopher\\-([0-9]*)\\;\\sMeals\\=([0-9]*)\\]'
    reg = re.compile('(%s)\\s%s\\:\\s(.*)' % (time, phil))
    match = re.match(reg, line).groups()
    return {
        'time': match[0],
        'phil': match[1],
        'meal': match[2],
        'msg': match[3]
    }

class LogicError(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)

def main():
    if len(sys.argv) <= 1:
        print("No files supplied")
        sys.exit(-1)

    for filename in sys.argv[1:]:
        try:
            print('Testing file', filename)
            with open(filename) as f:
                forks = set()
                chairs = set()

                linecounter = 1
                for line in f:
                    if line.find('#') != -1:
                        break

                    chairfound = re.compile('Found a nice seat \\(Chair\\-([0-9]*)\\)')
                    chairleave = re.compile('Stand up from seat \\(Chair\\-([0-9]*)\\)')

                    # Found 2 forks (Fork-2 from Chair-2, Fork-1 from Chair-1)! :D
                    forktake = re.compile('Found 2 forks \\(Fork\\-([0-9]*)\\ from Chair\\-([0-9]*)\\, Fork\\-([0-9]*)\\ from Chair\\-([0-9]*)\\)')
                    # Release my forks (Fork-1 from Chair-1, Fork-5 from Chair-5)
                    forkput = re.compile('Release my forks \\(Fork\\-([0-9]*)\\ from Chair\\-([0-9]*)\\, Fork\\-([0-9]*)\\ from Chair\\-([0-9]*)\\)')

                    l = parse_line(line)

                    msg = l['msg']

                    if 'Found a nice seat' in msg:
                        mat = re.match(chairfound, msg)

                        if mat.group(1) in chairs:
                            raise LogicError('Chair ' + mat.group(1) + ' already occupied in line ' + linecounter)

                        chairs.add(mat.group(1))
                    elif 'Stand up from seat' in msg:
                        mat = re.match(chairleave, msg)

                        if mat.group(1) not in chairs:
                            raise LogicError('Chair ' + mat.group(1) + ' not occupied in line ' + linecounter)

                        chairs.remove(mat.group(1))

                    elif 'Picked up fork' in msg:
                        mat = re.match(forktake, msg)

                        if mat.group(1) in forks:
                            raise LogicError('Fork ' + mat.group(1) + ' already occupied in line ' + linecounter)

                        if mat.group(2) in forks:
                            raise LogicError('Fork ' + mat.group(2) + ' already occupied in line ' + linecounter)

                        forks.add(mat.group(1))
                        forks.add(mat.group(2))
                    elif 'Release my fork' in msg:
                        mat = re.match(forkput, msg)

                        if mat.group(1) not in forks:
                            raise LogicError('Fork ' + mat.group(1) + ' not occupied in line ' + linecounter)

                        if mat.group(2) not in forks:
                            raise LogicError('Fork ' + mat.group(1) + ' not occupied in line ' + linecounter)

                        forks.remove(mat.group(1))
                        forks.remove(mat.group(2))

                    linecounter += 1
        except LogicError as err:
            print('Failure! ', err)

    print('Finished')
    input()

if __name__ == '__main__':
    main()
