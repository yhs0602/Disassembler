// Use capstone library, taken from Android-Disassembler

#include <string.h>
#include <jni.h>
#include <cxxabi.h>

extern "C" {
#include "capstone/include/capstone.h"
}

#include <sstream>
#include <string>
#include <stdio.h>
#include <stdlib.h>

using namespace std;

//#define CODE "\xED\xFF\xFF\xEB\x04\xe0\x2d\xe5\x00\x00\x00\x00\xe0\x83\x22\xe5\xf1\x02\x03\x0e\x00\x00\xa0\xe3\x02\x30\xc1\xe7\x00\x00\x53\xe3\x00\x02\x01\xf1\x05\x40\xd0\xe8\xf4\x80\x00\x00"

#include <vector>
vector<csh> handles;

extern "C" {
const char *errmsg(cs_err e);

int arch = CS_ARCH_ARM;

static void print_insn_detail(jint handle, string &buf, cs_insn *ins);

const char *errmsg(cs_err e) {
    switch (e) {
        case CS_ERR_OK:
            return "No error: everything was fine";
        case CS_ERR_MEM:
            return "Out-Of-Memory error: cs_open(), cs_disasm(), cs_disasm_iter()";
        case CS_ERR_ARCH:
            return "Unsupported architecture: cs_open()";
        case CS_ERR_HANDLE:
            return "Invalid handle: cs_op_count(), cs_op_index()";
        case CS_ERR_CSH:
            return "Invalid csh argument: cs_close(), cs_errno(), cs_option()";
        case CS_ERR_MODE:
            return "Invalid/unsupported mode: cs_open()";
        case CS_ERR_OPTION:
            return "Invalid/unsupported option: cs_option()";
        case CS_ERR_DETAIL:
            return "Information is unavailable because detail option is OFF";
        case CS_ERR_MEMSETUP:
            return "Dynamic memory management uninitialized (see CS_OPT_MEM)";
        case CS_ERR_VERSION:
            return "Unsupported version (bindings)";
        case CS_ERR_DIET:
            return "Access irrelevant data in diet engine";
        case CS_ERR_SKIPDATA:
            return "Access irrelevant data for data instruction in SKIPDATA mode";
        case CS_ERR_X86_ATT:
            return "X86 AT&T syntax is unsupported (opt-out at compile time)";
        case CS_ERR_X86_INTEL:
            return "X86 Intel syntax is unsupported (opt-out at compile time)";
        default:
            return "unsupported error message";
    }
}
JNIEXPORT jint JNICALL
Java_com_yhs0602_disassembler_MainActivity_Init(JNIEnv *env, jobject thiz) {
    cs_err e;
    cs_opt_mem mem;
    mem.malloc = malloc;
    mem.calloc = calloc;
    mem.free = free;
    mem.vsnprintf = vsnprintf;
    mem.realloc = realloc;
    cs_option(NULL, CS_OPT_MEM, (size_t) &mem);
    handles.clear();
//    handle = 0;
    return 0;
}

/**
 *
 * @param env
 * @param thiz
 * @param arch1
 * @param mode
 * @return cs_handle index
 */
JNIEXPORT jint JNICALL
Java_com_kyhsgeekcode_disassembler_MainActivity_Open(JNIEnv *env, jclass thiz, jint arch1,
                                                     jint mode) {
    cs_err e;
    csh handle = 0;
    cs_close(&handle);
    if ((e = cs_open((cs_arch) arch1, (cs_mode) mode, &handle)) != CS_ERR_OK) {
        return /* env->NewStringUTF(errmsg(e));*/e;
    }
    cs_option(handle, CS_OPT_DETAIL, CS_OPT_ON);
    // turn on SKIPDATA mode
    cs_option(handle, CS_OPT_SKIPDATA, CS_OPT_ON);
    arch = arch1;
    handles.push_back(handle);
    return handles.size()-1;
}
JNIEXPORT void JNICALL
Java_com_kyhsgeekcode_disassembler_MainActivity_Finalize(JNIEnv *env, jclass thiz, jint handleIndex) {
    csh handle = handles[handleIndex];
    cs_close(&handle);
    handle = 0;
    vector<csh>::iterator it = handles.begin();
    advance(it, (size_t)handleIndex);
    handles.erase(it);
}

struct platform {
    cs_arch arch;
    cs_mode mode;
    unsigned char *code;
    size_t size;
    char *comment;
    int syntax;
};

int cs_setup_mem() {
    cs_err e;
    cs_opt_mem mem;
    mem.malloc = malloc;
    mem.calloc = calloc;
    mem.free = free;
    mem.vsnprintf = vsnprintf;
    mem.realloc = realloc;
    return cs_option(NULL, CS_OPT_MEM, (size_t) &mem);
    //return 0;
}

JNIEXPORT jint JNICALL
Java_com_kyhsgeekcode_disassembler_DisasmIterator_CSoption(JNIEnv *env, jobject thiz, jint handleIndex,
                                                           jint arg1,
                                                           jint arg2) {
    if (arg1 == CS_OPT_MODE) {
        arch = arg2;
    }
    csh handle = handles[handleIndex];
    return (int) cs_option(handle, (cs_opt_type) arg1, arg2);
}


#include <unistd.h>
static int messagePipe[2];

//No no.should fill adapter.
//@returns the offset to resume later
JNIEXPORT jlong JNICALL
Java_com_kyhsgeekcode_disassembler_DisasmIterator_getSome(JNIEnv *env, jobject thiz,
                                                          jint handleIndex,
                                                          jbyteArray bytes, jlong offset,
                                                          jlong size, jlong virtaddr,
                                                          jint count/*, jobject arr*/) {

    csh handle = handles[handleIndex];

    int bytelen = env->GetArrayLength(bytes);
    jbyte *byte_buf;
    byte_buf = env->GetByteArrayElements(bytes, NULL);
    //jclass longcls = env->FindClass("java/lang/Long");
    //	jclass mapcls = env->FindClass("java/util/Map");
    jclass darcls = env->FindClass("com/kyhsgeekcode/disassembler/DisasmResult");
    jclass lvicls = env->FindClass("com/kyhsgeekcode/disassembler/DisassemblyListItem");
    jclass thecls = env->GetObjectClass(thiz);
    jmethodID ctor = env->GetMethodID(darcls, "<init>", "()V");
    jmethodID ctorLvi = env->GetMethodID(lvicls, "<init>",
                                         "(Lcom/kyhsgeekcode/disassembler/DisasmResult;)V");
    //jmethodID ctorLong = env->GetMethodID(longcls,"<init>","(Ljava/lang/Long;)V");
    //jmethodID java_util_List_add  = env->GetMethodID(mapcls, "add", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    //	jmethodID java_util_Map_put  = env->GetMethodID(mapcls, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    jmethodID notify = env->GetMethodID(thecls, "isCancel", "(I)I");
    
    jmethodID additem = env->GetMethodID(thecls, "AddItem",
                                         "(Lcom/kyhsgeekcode/disassembler/DisassemblyListItem;)V");
    int done = 0;
    // allocate memory cache for 1 instruction, to be used by cs_disasm_iter later.
    cs_insn *insn = cs_malloc(handle);
    

    const uint8_t *code = (uint8_t *) (byte_buf + offset);
    size_t code_size = size - offset;    // size of @code buffer above
    uint64_t addr = virtaddr;    // address of first instruction to be disassembled

    jfieldID fidMnemonic = env->GetFieldID(darcls, "mnemonic", "Ljava/lang/String;");
    

    if (fidMnemonic == NULL) {
        return -1; /* failed to find the field */
    }
    jfieldID fidOPStr = env->GetFieldID(darcls, "op_str", "Ljava/lang/String;");
    if (fidOPStr == NULL) {
        return -1; /* failed to find the field */
    }
    jfieldID fidAddr = env->GetFieldID(darcls, "address", "J");
    if (fidAddr == NULL) {
        return -1; /* failed to find the field */
    }
    jfieldID fidID = env->GetFieldID(darcls, "id", "I");
    if (fidID == NULL) {
        return -1; /* failed to find the field */
    }
    jfieldID fidSize = env->GetFieldID(darcls, "size", "I");
    if (fidSize == NULL) {
        return -1; /* failed to find the field */
    }
    jfieldID fidbytes = env->GetFieldID(darcls, "bytes", "[B");
    if (fidbytes == NULL) {
        return -1; /* failed to find the field */
    }
    jfieldID fidGroup = env->GetFieldID(darcls, "groups", "[B");
    if (fidGroup == NULL) {
        return -1; /* failed to find the field */
    }
    jfieldID fidGroupCount = env->GetFieldID(darcls, "groups_count", "B");
    if (fidGroupCount == NULL) {
        return -1; /* failed to find the field */
    }
    jfieldID fidJumpOffset = env->GetFieldID(darcls, "jumpOffset", "J");
    if (fidJumpOffset == NULL) {
        return -1; /* failed to find the field */
    }
    jfieldID fidArch = env->GetFieldID(darcls, "arch", "I");
    if (fidArch == NULL) {
        return -1; /* failed to find the field */
    }
    //int counter=0;
    // disassemble one instruction a time & store the result into @insn variable above
    while (cs_disasm_iter(handle, &code, &code_size, &addr, insn) && done < count) {
        // analyze disassembled instruction in @insn variable ...
        // NOTE: @code, @code_size & @address variables are all updated
        // to point to the next instruction after each iteration.
        //
        jobject dar = env->NewObject(darcls, ctor);
        env->SetIntField(dar, fidArch, arch);

        /* Create a new string and overwrite the instance field */
        jstring jstr = env->NewStringUTF(insn->mnemonic);
        if (jstr == NULL) {
            return -2; /* out of memory */
        }
        env->SetObjectField(dar, fidMnemonic, jstr);
        env->DeleteLocalRef(jstr);

        /* Create a new string and overwrite the instance field */
        jstr = env->NewStringUTF(insn->op_str);
        if (jstr == NULL) {
            return -2; /* out of memory */
        }
        env->SetObjectField(dar, fidOPStr, jstr);
        env->DeleteLocalRef(jstr);
        env->SetLongField(dar, fidAddr, (long long int)(insn->address));

        env->SetIntField(dar, fidID, insn->id);

        env->SetIntField(dar, fidSize, insn->size);
        jobject job = env->GetObjectField(dar, fidbytes);
        jbyteArray *jba = reinterpret_cast<jbyteArray *>(&job);
        int sz = env->GetArrayLength(*jba);
        // Get the elements (you probably have to fetch the length of the array as well
        jbyte *data = env->GetByteArrayElements(*jba, NULL);
        int min = insn->size > sz ? sz : insn->size;
        memcpy(data, insn->bytes, min);
        /*for(int i=0;i<min;++i)
        {
            data[i]=insn->bytes[i];
        }*/
        // Don't forget to release it
        env->ReleaseByteArrayElements(*jba, data, 0);
        env->DeleteLocalRef(job);
        //
        if (insn[0].detail != NULL) {
            const cs_detail *detail = insn->detail;
            jobject job2 = env->GetObjectField(dar, fidGroup);
            jbyteArray *jba2 = reinterpret_cast<jbyteArray *>(&job2);
            int sz2 = env->GetArrayLength(*jba2);
            // Get the elements (you probably have to fetch the length of the array as well
            jbyte *data2 = env->GetByteArrayElements(*jba2, NULL);
            int min = detail->groups_count > sz2 ? sz2 : detail->groups_count;
            memcpy(data2, detail->groups, min);
            /*for(int i=0;i<min;++i)
            {
                data2[i]=insn->detail->groups[i];
            }*/
            // Don't forget to release it
            env->ReleaseByteArrayElements(*jba2, data2, 0);
            env->DeleteLocalRef(job2);
            env->SetByteField(dar, fidGroupCount, detail->groups_count);
            //now get the operands, etc..
            long jumpOffset = 0;
            switch (arch) {
                case CS_ARCH_X86:    // X86 architecture (including x86 & x86-64)
                {
                    const cs_x86 *x86 = &detail->x86;
                    jumpOffset = X86_REL_ADDR(*insn) - insn->address;
                }
                    break;// IMPORTANT!!!!!!!!!!!!!!!!
                case CS_ARCH_ARM:    // ARM architecture (including Thumb, Thumb-2)
                {
                    const cs_arm *arm = &detail->arm;
                    switch (arm->op_count) {
                        case 0:
                            break;
                        case 1:    //B xx
                            jumpOffset =
                                    arm->operands[0].type == ARM_OP_IMM ? (arm->operands[0].imm -
                                                                           insn->address) : 0;
                            break;
                        case 2: //mov pc,#0
                            jumpOffset =
                                    arm->operands[1].type == ARM_OP_IMM ? (arm->operands[1].imm -
                                                                           insn->address) : 0;
                            break;
                            //TODO: parse PLT
                    }
                }
                    break;
                case CS_ARCH_ARM64:        // ARM-64, also called AArch64
                {
                    const cs_arm64 *arm64 = &detail->arm64;
                    switch (arm64->op_count) {
                        case 0:
                            break;
                        case 1:    //B xx
                            jumpOffset = arm64->operands[0].type == ARM64_OP_IMM ? (
                                    arm64->operands[0].imm - insn->address) : 0;
                            break;
                        case 2: //mov pc,#0
                            jumpOffset = arm64->operands[1].type == ARM64_OP_IMM ? (
                                    arm64->operands[1].imm - insn->address) : 0;
                            break;
                            //TODO: parse PLT
                    }

                }
                    break;
                case CS_ARCH_MIPS:        // Mips architecture
                {
                    const cs_mips *mips = &detail->mips;
                }
                    break;
                case CS_ARCH_PPC:        // PowerPC architecture
                {
                    const cs_ppc *ppc = &detail->ppc;
                }
                    break;
                case CS_ARCH_SPARC:        // Sparc architecture
                {
                    const cs_sparc *sparc = &detail->sparc;
                }
                    break;
                case CS_ARCH_SYSZ:        // SystemZ architecture
                {
                    const cs_sysz *sysz = &detail->sysz;
                }
                    break;
                case CS_ARCH_XCORE:        // XCore architecture
                {
                    const cs_xcore *xcore = &detail->xcore;
                }
                    break;
            }
            env->SetLongField(dar, fidJumpOffset, jumpOffset);
        }
        jobject lvi = env->NewObject(lvicls, ctorLvi, dar);
        env->CallVoidMethod(thiz, additem, lvi);

        env->DeleteLocalRef(lvi);
        env->DeleteLocalRef(dar);
        if (done % 1024 == 0) {
            int ret = env->CallIntMethod(thiz, notify, done);
            if (ret == -1) {
                //thread interrupted
                break;
            }
        }
        ++done;
    }
    // release the cache memory when done
    cs_free(insn, 1);
    env->ReleaseByteArrayElements(bytes, byte_buf, JNI_ABORT);
    return (jlong) ((long) code - (long) byte_buf);//return the number of processed bytes
}
JNIEXPORT jlong JNICALL
Java_com_kyhsgeekcode_disassembler_DisasmIterator_getAll(JNIEnv *env, jobject thiz,
                                                         jint handleIndex,
                                                         jbyteArray bytes, jlong offset, jlong size,
                                                         jlong virtaddr/*, jobject arr*/) {
    return Java_com_kyhsgeekcode_disassembler_DisasmIterator_getSome(env, thiz, handleIndex, bytes, offset, size,
                                                                     virtaddr, 2148483647);
}
//public NativeLong cs_disasm2(NativeLong handle, byte[] code, NativeLong code_offset,NativeLong code_len,
//						long addr, NativeLong count, PointerByReference insn);
CAPSTONE_EXPORT size_t CAPSTONE_API cs_disasm2(csh handle, const uint8_t *code, size_t code_offset,
                                               size_t code_size, uint64_t address, size_t count,
                                               cs_insn **insn) {
    return cs_disasm(handle, (const uint8_t *) (code + code_offset), code_size - code_offset,
                     address, count, insn);
}
JNIEXPORT jstring JNICALL
Java_com_kyhsgeekcode_disassembler_ElfFile_Demangle(JNIEnv *env, jclass thiz, jstring mangled) {
    const char *cstr = env->GetStringUTFChars(mangled, NULL);
    char *demangled_name;
    int status = -1;
    demangled_name = abi::__cxa_demangle(cstr, NULL, NULL, &status);
    jstring ret = env->NewStringUTF(demangled_name);
    //printf("Demangled: %s\n", demangled_name);
    free(demangled_name);
    env->ReleaseStringUTFChars(mangled, cstr);
    return ret;
}

//#include"plthook/plthook.h"
JNIEXPORT jobject JNICALL
Java_com_kyhsgeekcode_disassembler_ElfFile_ParsePLT(JNIEnv *env, jclass thiz, jstring filepath) {
    const char *filename = env->GetStringUTFChars(filepath, NULL);
    jclass pltcls = env->FindClass("com/kyhsgeekcode/disassembler/ImportSymbol");
    
    jclass listcls = env->FindClass("java/util/ArrayList");
    
    //jclass thecls = env->GetObjectClass(thiz);
    //
    jmethodID ctor = env->GetMethodID(pltcls, "<init>", "()V");
    
    jmethodID ctorList = env->GetMethodID(listcls, "<init>", "()V");
    //jmethodID ctorLong = env->GetMethodID(longcls,"<init>","(Ljava/lang/Long;)V");
    //jmethodID java_util_List_add  = env->GetMethodID(mapcls, "add", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    
    //	jmethodID java_util_Map_put  = env->GetMethodID(mapcls, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    //
    jmethodID java_util_List_add = env->GetMethodID(listcls, "add", "(Ljava/lang/Object;)Z");
    jobject listobj = env->NewObject(listcls, ctorList);
    ///plthook_t *plthook;
    unsigned int pos = 0; /* This must be initialized with zero. */
    const char *name;
    void **addr;
    //if (plthook_open(&plthook, filename) != 0) {
    //    
    //                       plthook_error());
    return NULL;
    //}
    jfieldID fidName = env->GetFieldID(pltcls, "name", "Ljava/lang/String;");
    if (fidName == NULL) {
        return NULL; /* failed to find the field */
    }
    jfieldID fidAddress = env->GetFieldID(pltcls, "address", "J");
    if (fidAddress == NULL) {
        return NULL; /* failed to find the field */
    }
    jfieldID fidValue = env->GetFieldID(pltcls, "value", "J");
    if (fidValue == NULL) {
        return NULL; /* failed to find the field */
    }
    // while (plthook_enum(plthook, &pos, &name, &addr) == 0) {
    jobject plt = env->NewObject(pltcls, ctor);
    jstring jname = env->NewStringUTF(name);
    env->SetObjectField(plt, fidName, jname);
    env->DeleteLocalRef(jname);
    env->SetLongField(plt, fidAddress, (long) addr);
    env->SetLongField(plt, fidValue, *((unsigned long *) addr));
    env->CallBooleanMethod(listobj, java_util_List_add, plt);
    env->DeleteLocalRef(plt);
    
    //}
    //plthook_close(plthook);
    env->ReleaseStringUTFChars(filepath, filename);

    return listobj;
}

static void print_string_hex(string buf, char *comment, unsigned char *str, size_t len) {
    unsigned char *c;
    char *b;
    asprintf(&b, "%s", comment);
    buf += b;
    free(b);
    for (c = str; c < str + len; c++) {
        asprintf(&b, "0x%02x ", *c & 0xff);
        buf += b;
        free(b);
    }
    buf += "\n";
}
static void print_insn_detail(jint handle, string &buf, cs_insn *ins) {
    cs_arm *arm;
    int i;
    char *b;
    // detail can be NULL on "data" instruction if SKIPDATA option is turned ON
    if (ins->detail == NULL)
        return;

    arm = &(ins->detail->arm);

    /*if (arm->op_count){
        asprintf(&b,"\top_count: %u\n", arm-> op_count);
        buf+=b;
        free(b);
    }*/
    for (i = 0; i < arm->op_count; i++) {
        cs_arm_op *op = &(arm->operands[i]);
        switch ((int) op->type) {
            default:
                break;
            case ARM_OP_REG:
                asprintf(&b, "\t\toperands[%u].type: REG = %s\n", i, cs_reg_name(handle, op->reg));
                buf += b;
                free(b);
                break;
            case ARM_OP_IMM:
                asprintf(&b, "\t\toperands[%u].type: IMM = 0x%x\n", i, op->imm);
                buf += b;
                free(b);
                break;
            case ARM_OP_FP:
#if defined(_KERNEL_MODE)
                // Issue #681: Windows kernel does not support formatting float point
                    asprintf(&b,"\t\toperands[%u].type: FP = <float_point_unsupported>\n", i);
                    buf+=b;
                    free(b);
#else
                asprintf(&b, "\t\toperands[%u].type: FP = %f\n", i, op->fp);
                buf += b;
                free(b);
#endif
                break;
            case ARM_OP_MEM:
                asprintf(&b, "\t\toperands[%u].type: MEM\n", i);
                buf += b;
                free(b);
                if (op->mem.base != ARM_REG_INVALID) {
                    asprintf(&b, "\t\t\toperands[%u].mem.base: REG = %s\n",
                             i, cs_reg_name(handle, op->mem.base));
                    buf += b;
                    free(b);
                }
                if (op->mem.index != ARM_REG_INVALID) {
                    asprintf(&b, "\t\t\toperands[%u].mem.index: REG = %s\n",
                             i, cs_reg_name(handle, op->mem.index));
                    buf += b;
                    free(b);
                }
                if (op->mem.scale != 1) {
                    asprintf(&b, "\t\t\toperands[%u].mem.scale: %u\n", i, op->mem.scale);
                    buf += b;
                    free(b);
                }
                if (op->mem.disp != 0) {
                    asprintf(&b, "\t\t\toperands[%u].mem.disp: 0x%x\n", i, op->mem.disp);
                    buf += b;
                    free(b);
                }
                break;
            case ARM_OP_PIMM:
                asprintf(&b, "\t\toperands[%u].type: P-IMM = %u\n", i, op->imm);
                buf += b;
                free(b);
                break;
            case ARM_OP_CIMM:
                asprintf(&b, "\t\toperands[%u].type: C-IMM = %u\n", i, op->imm);
                buf += b;
                free(b);
                break;
            case ARM_OP_SETEND:
                asprintf(&b, "\t\toperands[%u].type: SETEND = %s\n", i,
                         op->setend == ARM_SETEND_BE ? "be" : "le");
                buf += b;
                free(b);
                break;
            case ARM_OP_SYSREG:
                asprintf(&b, "\t\toperands[%u].type: SYSREG = %u\n", i, op->reg);
                buf += b;
                free(b);
                break;
        }
        //buf+=b;
        //free(b);
        if (op->shift.type != ARM_SFT_INVALID && op->shift.value) {
            if (op->shift.type < ARM_SFT_ASR_REG)
                // shift with constant value
                asprintf(&b, "\t\t\tShift: %u = %u\n", op->shift.type, op->shift.value);

            else
                // shift with register
                asprintf(&b, "\t\t\tShift: %u = %s\n", op->shift.type,
                         cs_reg_name(handle, op->shift.value));
            buf += b;
            free(b);
        }

        if (op->vector_index != -1) {
            asprintf(&b, "\t\toperands[%u].vector_index = %u\n", i, op->vector_index);
            buf += b;
            free(b);
        }

        if (op->subtracted) {
            asprintf(&b, "\t\tSubtracted: True\n");
            buf += b;
            free(b);
        }
    }

    if (arm->cc != ARM_CC_AL && arm->cc != ARM_CC_INVALID) {
        asprintf(&b, "\tCode condition: %u\n", arm->cc);
        buf += b;
        free(b);
    }
    if (arm->update_flags) {
        asprintf(&b, "\tUpdate-flags: True\n");
        buf += b;
        free(b);
    }
    if (arm->writeback) {
        asprintf(&b, "\tWrite-back: True\n");
        buf += b;
        free(b);
    }
    if (arm->cps_mode) {
        asprintf(&b, "\tCPSI-mode: %u\n", arm->cps_mode);
        buf += b;
        free(b);
    }
    if (arm->cps_flag) {
        asprintf(&b, "\tCPSI-flag: %u\n", arm->cps_flag);
        buf += b;
        free(b);
    }
    if (arm->vector_data) {
        asprintf(&b, "\tVector-data: %u\n", arm->vector_data);
        buf += b;
        free(b);
    }
    if (arm->vector_size) {
        asprintf(&b, "\tVector-size: %u\n", arm->vector_size);
        buf += b;
        free(b);
    }
    if (arm->usermode) {
        asprintf(&b, "\tUser-mode: True\n");
        buf += b;
        free(b);
    }
    if (arm->mem_barrier) {
        asprintf(&b, "\tMemory-barrier: %u\n", arm->mem_barrier);
        buf += b;
        free(b);
    }
    buf += "\n";
}
}


#include "elfio/elfio_dump.hpp"
#include "elfio/elf_types.hpp"
#include "elfio/elfio_section.hpp"
#include "elfio/elfio_symbols.hpp"

using namespace ELFIO;

struct DisassemblerSymbol {
    std::string name;
    Elf64_Addr value;
    Elf_Xword size;
    unsigned char bind;
    unsigned char type;
    Elf_Half section;
    unsigned char other;
};

//std::vector<DisassemblerSymbol>disassemblerSymbolsList;

//Pure
void loadSymbols(const elfio &reader, JNIEnv *env, jobject thiz) {
    jclass symbolcls = env->FindClass("com/kyhsgeekcode/disassembler/Symbol");
    
    jclass listcls = env->FindClass("java/util/ArrayList");
    
    jclass thecls = env->GetObjectClass(thiz);
    jmethodID ctor = env->GetMethodID(symbolcls, "<init>", "()V");
    jmethodID addSymbol = env->GetMethodID(thecls, "addSymbol",
                                           "(Lcom/kyhsgeekcode/disassembler/Symbol;)V");
    jfieldID fieldName = env->GetFieldID(symbolcls, "name", "Ljava/lang/String;");
    jfieldID fieldValue = env->GetFieldID(symbolcls, "st_value", "J");
    jfieldID fieldSize = env->GetFieldID(symbolcls, "st_size", "J");
    jfieldID fieldInfo = env->GetFieldID(symbolcls, "st_info", "S");
//    jfieldID fieldBind = env->GetFieldID(symbolcls, "bind", "J");
//    jfieldID fieldType = env->GetFieldID(symbolcls, "type","J");
    jfieldID fieldSection = env->GetFieldID(symbolcls, "st_shndx", "S");
    jfieldID fieldOther = env->GetFieldID(symbolcls, "st_other", "S");

    jclass importSymbolClass = env->FindClass("com/kyhsgeekcode/disassembler/ImportSymbol");
    jmethodID ctorImport = env->GetMethodID(importSymbolClass, "<init>","()V");
    jmethodID addImportSymbol = env->GetMethodID(thecls,"addImportSymbol","(Lcom/kyhsgeekcode/disassembler/ImportSymbol;)V");
    jfieldID fieldOffset = env->GetFieldID(importSymbolClass, "offset", "J");
    jfieldID fieldSymbolValue = env->GetFieldID(importSymbolClass, "value", "J");
    jfieldID fieldSymbolName = env->GetFieldID(importSymbolClass, "name", "Ljava/lang/String;");
    jfieldID fieldSymbolType = env->GetFieldID(importSymbolClass, "type", "I");
    jfieldID fieldSymbolAddEnd = env->GetFieldID(importSymbolClass, "addend", "J");
    jfieldID fieldSymbolCalcValue = env->GetFieldID(importSymbolClass, "calcValue", "J");

//    disassemblerSymbolsList.clear();
    Elf_Half n = reader.sections.size();
    for (Elf_Half i = 0; i < n; ++i) {
        section *sec = reader.sections[i];
        if (SHT_SYMTAB == sec->get_type() || SHT_DYNSYM == sec->get_type()) {
            symbol_section_accessor symbols(reader, sec);
            long long sym_no = symbols.get_symbols_num();
            if (sym_no > 0) {
                for (long long i = 0; i < sym_no; ++i) {
                    DisassemblerSymbol symbol_disassembler;
                    symbols.get_symbol(static_cast<Elf_Xword>(i), symbol_disassembler.name,
                                       symbol_disassembler.value, symbol_disassembler.size,
                                       symbol_disassembler.bind, symbol_disassembler.type,
                                       symbol_disassembler.section, symbol_disassembler.other);
                    short st_info = symbol_disassembler.bind << 4 | symbol_disassembler.type;
                    jobject sym = env->NewObject(symbolcls, ctor);
                    jstring jname = env->NewStringUTF(symbol_disassembler.name.c_str());
                    env->SetObjectField(sym, fieldName, jname);
                    env->SetLongField(sym, fieldValue, symbol_disassembler.value);
                    env->SetLongField(sym, fieldSize, symbol_disassembler.size);
                    env->SetShortField(sym, fieldInfo, st_info);
                    env->SetShortField(sym, fieldSection, symbol_disassembler.section);
                    env->SetShortField(sym, fieldOther, symbol_disassembler.other);
                    env->CallVoidMethod(thiz, addSymbol, sym);
                    env->DeleteLocalRef(sym);
                    env->DeleteLocalRef(jname);
//                    disassemblerSymbolsList.push_back(symbol_disassembler);
                }
            }
        } else if(SHT_REL == sec->get_type() || SHT_RELA == sec->get_type()) {
            relocation_section_accessor relocations(reader, sec);
            long long rel_no = relocations.get_entries_num();
            if (rel_no > 0) {
                for (long long i = 0; i < rel_no; ++i) {
                    Elf64_Addr offset;
                    Elf64_Addr symbolValue;
                    std::string symbolName;
                    Elf_Word type;
                    Elf_Sxword addend;
                    Elf_Sxword calcValue;
                    relocations.get_entry(static_cast<Elf_Xword>(i), offset,
                            symbolValue, symbolName, type, addend, calcValue);
                    jobject sym = env->NewObject(importSymbolClass, ctorImport);
                    jstring jname = env->NewStringUTF(symbolName.c_str());
                    env->SetLongField(sym, fieldOffset, offset);
                    env->SetLongField(sym, fieldSymbolValue, symbolValue);
                    env->SetObjectField(sym, fieldSymbolName, jname);
                    env->SetIntField(sym, fieldSymbolType, type);
                    env->SetLongField(sym, fieldSymbolAddEnd, addend);
                    env->SetLongField(sym, fieldSymbolCalcValue, calcValue);
                    env->CallVoidMethod(thiz, addImportSymbol, sym);
                    env->DeleteLocalRef(sym);
                    env->DeleteLocalRef(jname);
                }
            }
        }
    }
}

extern "C" {
JNIEXPORT void JNICALL
Java_com_kyhsgeekcode_disassembler_ElfFile_loadBinary(JNIEnv *env, jobject thiz, jstring path) {
    elfio reader;
    const char *filename = env->GetStringUTFChars(path, NULL);
    reader.load(filename);
    loadSymbols(reader, env, thiz);
}
}
