/*
 * $Id: Exception.h 3 2011-08-19 02:25:45Z  $
 */
#ifndef LWPR_EXCEPTION_H__
#define LWPR_EXCEPTION_H__
#include "Utility.h"
#include <cerrno>

#if defined(WIN32) || defined(LINUX)
#define EXCEPTION_TRACE   \
	__FILE__,   \
	__LINE__,   \
	__FUNCTION__, \
	errno

#define EXCEPTION_THREAD_TRACE   \
	__FILE__,   \
	__LINE__,   \
	__FUNCTION__
#endif

namespace LWPR
{
	class Exception : public std::exception
	{
	public:
		Exception(const char *msg = "LWPRException") throw()
			: m_strExcep(msg), m_errno(errno)
		{
		}

		Exception(const char *file,
		          int line,
		          const char *fun,
		          int err_no,
		          const char *dsc = NULL) throw()
			: m_strExcep(Utility::ExceptionInfo(file, line, fun, err_no, "LWPRException", dsc).c_str()),
			  m_errno(errno)
		{
		}

		virtual const char * what() const throw()
		{
			return m_strExcep.c_str();
		}

		int GetErrno()
		{
			return (m_errno);
		}

		virtual ~Exception() throw()
		{
		}
	protected:
		std::string         m_strExcep;
		int                 m_errno;
	};

};

#define DEFINE_EXCEPTION(name) \
class name : public LWPR::Exception \
{   \
public: \
	name(const char *msg = #name) throw () \
	: Exception(msg) {} \
	name(const char *file, \
	int line,   \
	const char *fun,    \
	int errnum, \
	const char *dsc = NULL) throw ()    \
	: Exception(Utility::ExceptionInfo(file, line, fun, errnum, #name, dsc).c_str()) {} \
};

namespace LWPR
{
	DEFINE_EXCEPTION(LWPR_BAD_PARM);
	DEFINE_EXCEPTION(LWPR_NOT_IMPLEMENT);
};

#endif // end of LWPR_EXCEPTION_H__
