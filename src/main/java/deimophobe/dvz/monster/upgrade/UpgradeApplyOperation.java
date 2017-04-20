package deimophobe.dvz.monster.upgrade;

import java.rmi.UnexpectedException;

/**
 * Created by Deimophobe on 24/02/17.
 */
public enum UpgradeApplyOperation {
	INCREMENT {
		@Override
		public int apply(int prev, int value) {
			return prev++;
		}
	},
	ADD {
		@Override
		public int apply(int prev, int value) {
			return prev + value;
		}
	},
	SET {
		@Override
		public int apply(int prev, int value) {
			return value;
		}
	},
	SETTRUE {
		@Override
		public int apply(int prev, int value) {
			return 1;
		}
	},
	SETFALSE {
		@Override
		public int apply(int prev, int value) {
			return 0;
		}
	},
	;
	
	public int apply(int previous, int value) {
		throw new UnsupportedOperationException("UpgradeApplyOperation enum constant: '"+ name() + "' does not implement apply method.");
	}
	
	public static UpgradeApplyOperation getOperation(String name) {
		return valueOf(name.toUpperCase());
	}
}
