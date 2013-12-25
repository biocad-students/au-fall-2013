1. Fix ClustalO:
	in src\clustal\pair_dist.c line 304
	change
	if (NULL != fdist_in) {
		Log(&rLog, LOG_FATAL, "FIXME: reading of distance matrix from file not implemented");
	}
	to
	if (NULL != fdist_in) {
		//Log(&rLog, LOG_FATAL, "FIXME: reading of distance matrix from file not implemented");
		SymMatrixRead(fdist_in, (distmat));
	}

2. Pipeline:
	0 seq.fasta - source reads
	1 HomopolymerAlignment with seq.fasta => dist_matrix
	2 fixed ClustalO with dist_matrix => tree.nwk
	3 MergeTree with tree.nwk => merged.fasta
	4 ClustalO with merged_fasta => merged_tree.nwk