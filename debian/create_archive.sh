#!/bin/bash
#
# Creates an archive suitable for distribution (standard layout for binaries,
# libraries, etc.).

set -e

if [ -z ${PACKAGE_TITLE} -o -z ${VERSION} -o -z ${DESTDIR} ]; then
    echo "PACKAGE_TITLE, VERSION, and DESTDIR environment variables must be set."
    exit 1
fi

BINPATH=${PREFIX}/bin
LIBPATH=${PREFIX}/share/java/${PACKAGE_TITLE}
DOCPATH=${PREFIX}/share/doc/${PACKAGE_TITLE}

INSTALL="install -D -m 644"
INSTALL_X="install -D -m 755"

rm -rf ${DESTDIR}${PREFIX}
mkdir -p ${DESTDIR}${PREFIX}
mkdir -p ${DESTDIR}${BINPATH}
mkdir -p ${DESTDIR}${LIBPATH}
mkdir -p ${DESTDIR}${SYSCONFDIR}

PREPACKAGED="camus-example/target/"
pushd ${PREPACKAGED}
find . -type f | grep shaded[.]jar | xargs -I XXX ${INSTALL_X} -o root -g root XXX ${DESTDIR}${LIBPATH}/XXX
cd classes
find . -type f | grep -E "^*[.]properties|xml" | xargs -I XXX ${INSTALL_X} -o root -g root XXX ${DESTDIR}${SYSCONFDIR}/XXX
cd ..
popd
