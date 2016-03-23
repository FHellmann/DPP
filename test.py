import re
import sys


def parse_line(line):
    time = '[0-9]{2}\\:[0-9]{2}\\:[0-9]{2}\\.[0-9]{3}'
    phil = '\\[Philosopher\\-([0-9]*)\\;\\sMeals\\=([0-9]*)\\]'
    reg = re.compile('(%s)\\s%s\\:\\s(.*)' % (time, phil))
    match = re.match(reg, line).groups()
    return {
        'time': match[0],
        'phil': match[1],
        'meal': match[2],
        'msg': match[3]
    }


def main():
    with open('logger-merged.txt') as f:
        forks = set()
        chairs = set()

        for line in f:
            chairfound = re.compile('Found a nice seat \\(Chair\\-([0-9]*)\\)')
            chairleave = re.compile('Stand up from seat \\(Chair\\-([0-9]*)\\)')


            l = parse_line(line)

            msg = l['msg']

            if 'Found a nice seat' in msg:
                mat = re.match(chairfound, msg)
                
                if mat.group(1) in chairs:
                    print('Chair', mat.group(1), 'already occupied')
                    sys.exit(1)

                chairs.add(mat.group(1))
            elif 'Stand up from seat' in msg:
                mat = re.match(chairleave, msg)

                if mat.group(1) not in chairs:
                    print('Chair', mat.group(1), 'not occupied')
                    sys.exit(1)

                chairs.remove(mat.group(1))


if __name__ == '__main__':
    main()
