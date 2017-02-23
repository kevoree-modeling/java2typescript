export namespace org {
    export namespace junit {
        export class Assert {
            public static assertArrayEquals(p: any, p2: any) {
                if (p == null || p == undefined) {
                    if (p2 == null || p2 == undefined) {
                        return;
                    } else {
                        throw new Error("Assert Error " + p + " and " + p2 + " must be equals");
                    }
                }
                if (p2 == null || p2 == undefined) {
                    if (p == null || p == undefined) {
                        return;
                    } else {
                        throw new Error("Assert Error " + p + " and " + p2 + " must be equals");
                    }
                }
                if (p.length != p2.length) {
                    throw new Error("Assert Error " + p + " and " + p2 + " must be equals");
                }
                for (var i = 0; i < p.length; i++) {
                    if (p[i] != p2[i]) {
                        throw new Error("Assert Error " + p + " and " + p2 + " must be equals");
                    }
                }
            }

            public static assertNotNull(p: any): void {
                if (p == null) {
                    throw new Error("Assert Error " + p + " must not be null");
                }
            }

            public static assertNull(p: any): void {
                if (p != null) {
                    throw new Error("Assert Error " + p + " must be null");
                }
            }

            public static assertEquals(p: any, p2: any, p3: string = ""): void {
                if (p == null) {
                    if (p2 == null) {
                        return;
                    } else {
                        throw new Error("Assert Error: " + p3 + "\n" + p + "\n must be equal to \n" + p2 + "\n");
                    }
                }
                if (p2 == null) {
                    if (p == null) {
                        return;
                    } else {
                        throw new Error("Assert Error: " + p3 + "\n" + p + "\n must be equal to \n" + p2 + "\n");
                    }
                }
                if (p.equals !== undefined) {
                    if (!p.equals(p2)) {
                        throw new Error("Assert Error: " + p3 + "\n" + p + "\n must be equal to \n" + p2 + "\n");
                    }
                } else {
                    if (p != p2) {
                        throw new Error("Assert Error: " + p3 + "\n" + p + "\n must be equal to \n" + p2 + "\n");
                    }
                }
            }

            public static assertNotEquals(p: any, p2: any, p3: string = ""): void {
                if (p == null) {
                    if (p2 == null) {
                        throw new Error("Assert Error: " + p3 + "\n" + p + "\n must not be equal to \n" + p2 + "\n");
                    } else {
                        return;
                    }
                }
                if (p2 == null) {
                    if (p == null) {
                        throw new Error("Assert Error: " + p3 + "\n" + p + "\n must not be equal to \n" + p2 + "\n");
                    } else {
                        return;
                    }
                }
                if (p.equals !== undefined) {
                    if (p.equals(p2)) {
                        throw new Error("Assert Error: " + p3 + "\n" + p + "\n must not be equal to \n" + p2 + "\n");
                    }
                } else {
                    if (p == p2) {
                        throw new Error("Assert Error: " + p3 + "\n" + p + "\n must not be equal to \n" + p2 + "\n");
                    }
                }
            }

            public static assertTrue(b: boolean): void {
                if (!b) {
                    throw new Error("Assert Error " + b + " must be true");
                }
            }

            public static assertFalse(b: boolean): void {
                if (b) {
                    throw new Error("Assert Error " + b + " must be false");
                }
            }

            public static fail(msg: string): void {
                throw new Error("Assert Error \n" + msg + "\n");
            }
        }
    }
}
