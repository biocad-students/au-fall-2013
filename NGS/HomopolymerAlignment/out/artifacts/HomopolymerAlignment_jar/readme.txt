The program aligns sequences with homopolymers.
The output is distance matrix in phylip format for ClustalO.

Usage:
    -h, --help, ?           Show this help
    -i <file>               Input FASTA file (necessary)
    -o <file>               Output file with distance matrix in phylip format (necessary)
    --score-matrix <file>   You may provide your own scoring matrix. "ScoreMatrix.txt" is used by default
    --gap <value>           Set gap score. By default, gap score = -4
    --large                 This mode works slower than usual but allows to operate with larger data.
                            Use if you have OutOfMemory exception. Disable by default.
    --threads <value>       Allows to use multithreading. By default, only one thread is used.

Examples:
    Main -i test.fasta -o output.txt
    Basic usage

    Main -i test.fasta -o output.txt --threads 4
    Working with 4 threads

    Main -i test.fasta -o output.txt --threads 2 --large
    Working with 2 threads with large data


