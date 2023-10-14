package edu.nyu.crypto.miners;

import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;

public class SelfishMiner extends CompliantMiner implements Miner {
	private Block currentHead;
	private Block currentMiningBlock;
	private NetworkStatistics networkStatisticsSnapshot;

	public SelfishMiner(String id, int hashRate, int connectivity) {
		super(id, hashRate, connectivity);
	}

	@Override
	public Block currentlyMiningAt() {
		return this.currentMiningBlock;
	}

	@Override
	public Block currentHead() {
		return this.currentHead;
	}

	@Override
	public void blockMined(Block block, boolean isMinerMe) {
		if (isMinerMe) {
			if (block.getHeight() > this.currentMiningBlock.getHeight()) {
				this.currentMiningBlock = block;
			}
		} else {
			if (block.getHeight() > this.currentMiningBlock.getHeight()) {
				this.currentMiningBlock = block;
				this.currentHead = block;
			} else if (block.getHeight() == currentMiningBlock.getHeight()) {
				this.currentHead = this.currentMiningBlock;
			} else {
				Block exposeBlock = this.currentMiningBlock;
				int diff = isBlockWorthCompeting() ? 0 : 1;
				while (exposeBlock != null && exposeBlock.getHeight() - block.getHeight() > diff) {
					if (exposeBlock.getPreviousBlock() == null) break;
					exposeBlock = exposeBlock.getPreviousBlock();
				}
				this.currentHead = exposeBlock;
			}
		}
	}

	private boolean isBlockWorthCompeting() {
		return this.getConnectivity() > networkStatisticsSnapshot.getTotalConnectivity() - this.getConnectivity();
//		double gamma = this.getConnectivity() / (double) networkStatisticsSnapshot.getTotalConnectivity();
//		double alpha = this.getHashRate() / (double) this.networkStatisticsSnapshot.getTotalHashRate();
//		return alpha > 0.5 || alpha > ((1.0 - gamma) / (3.0 - 2.0 * gamma));
	}

	@Override
	public void initialize(Block genesis, NetworkStatistics networkStatistics) {
		this.currentHead = genesis;
		this.currentMiningBlock = genesis;
		this.networkStatisticsSnapshot = networkStatistics;
	}

	@Override
	public void networkUpdate(NetworkStatistics statistics) {
		this.networkStatisticsSnapshot = statistics;
	}
}
